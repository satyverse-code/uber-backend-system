package com.uber.backend.service;

import com.uber.backend.dto.RideRequest;
import com.uber.backend.dto.RideResponse;
import com.uber.backend.model.Ride;
import com.uber.backend.repository.RideRepository;
import com.uber.backend.events.TripCreatedEvent;
import com.uber.backend.events.DriverAssignedEvent;
import com.uber.backend.kafka.TripEventProducer;
import com.uber.backend.store.ProcessedEvent;
import com.uber.backend.store.ProcessedEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RideService {

    private final RideRepository rideRepository;
    private final TripEventProducer tripEventProducer;
    private final ProcessedEventRepository processedEventRepository;

    public RideService(RideRepository rideRepository,
                       TripEventProducer tripEventProducer,
                       ProcessedEventRepository processedEventRepository) {
        this.rideRepository = rideRepository;
        this.tripEventProducer = tripEventProducer;
        this.processedEventRepository = processedEventRepository;
    }

    @Transactional
    public RideResponse createRide(RideRequest request) {
        Ride ride = new Ride();
        ride.setRiderId(request.getRiderId());
        ride.setSource(request.getSource());
        ride.setDestination(request.getDestination());
        ride.setStatus("REQUESTED");
        ride.setCreatedAt(LocalDateTime.now());

        Ride saved = rideRepository.save(ride);

        TripCreatedEvent event = new TripCreatedEvent(
                UUID.randomUUID().toString(),
                saved.getId(),
                saved.getRiderId(),
                saved.getSource(),
                saved.getDestination(),
                Instant.now()
        );
        tripEventProducer.publishTripCreated(event);

        RideResponse resp = new RideResponse();
        resp.setRideId(saved.getId());
        resp.setStatus(saved.getStatus());
        resp.setMessage("Ride created successfully");
        return resp;
    }

    public List<Ride> getRidesForRider(Long riderId) {
        return rideRepository.findByRiderId(riderId);
    }

    @Transactional
    public void assignDriver(Long tripId, Long driverId, Instant assignedAt, String eventId) {
        Optional<ProcessedEvent> existing = processedEventRepository.findById(eventId);
        if (existing.isPresent()) {
            return; // idempotent
        }
        Ride ride = rideRepository.findById(tripId)
                .orElseThrow(() -> new IllegalArgumentException("Ride not found: " + tripId));
        if (ride.getDriverId() == null) {
            ride.setDriverId(driverId);
            ride.setStatus("ASSIGNED");
            rideRepository.save(ride);
        }
        processedEventRepository.save(new ProcessedEvent(eventId, "driver_assigned"));
    }
}
