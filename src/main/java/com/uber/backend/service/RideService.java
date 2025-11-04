package com.uber.backend.service;

import com.uber.backend.dto.RideRequest;
import com.uber.backend.dto.RideResponse;
import com.uber.backend.model.Ride;
import com.uber.backend.repository.RideRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RideService {

    private final RideRepository rideRepository;

    public RideService(RideRepository rideRepository) {
        this.rideRepository = rideRepository;
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

        RideResponse resp = new RideResponse();
        resp.setRideId(saved.getId());
        resp.setStatus(saved.getStatus());
        resp.setMessage("Ride created successfully");
        return resp;
    }

    public List<Ride> getRidesForRider(Long riderId) {
        return rideRepository.findByRiderId(riderId);
    }
}
