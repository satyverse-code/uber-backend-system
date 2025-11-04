package com.uber.orchestrator.service;

import com.uber.orchestrator.client.BookingClient;
import com.uber.orchestrator.client.DriverClient;
import com.uber.orchestrator.dto.StartSagaRequest;
import com.uber.orchestrator.saga.Saga;
import com.uber.orchestrator.saga.SagaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrchestratorService {

    private final SagaRepository sagaRepository;
    private final BookingClient bookingClient;
    private final DriverClient driverClient;

    public OrchestratorService(SagaRepository sagaRepository, BookingClient bookingClient, DriverClient driverClient) {
        this.sagaRepository = sagaRepository;
        this.bookingClient = bookingClient;
        this.driverClient = driverClient;
    }

    @Transactional
    public Map<String, Object> startSaga(StartSagaRequest req) {
        String sagaId = UUID.randomUUID().toString();
        Long tripId = bookingClient.createRide(req.getRiderId(), req.getPickup(), req.getDropoff());
        Saga saga = new Saga(sagaId, tripId, "STARTED");
        saga.setPickupLat(req.getLat());
        saga.setPickupLng(req.getLng());
        saga.setUpdatedAt(Instant.now());
        sagaRepository.save(saga);
        return Map.of("sagaId", sagaId, "tripId", tripId, "state", saga.getState());
    }

    @Transactional
    public Map<String, Object> reserveDriver(String sagaId) {
        Saga saga = getSagaOrThrow(sagaId);
        Long driverId = driverClient.reserveNearest(
                saga.getPickupLat(),
                saga.getPickupLng(),
                5000.0
        );
        if (driverId == null) {
            saga.setState("CANCELLED");
            saga.setLastError("NO_DRIVER_AVAILABLE");
        } else {
            saga.setDriverId(driverId);
            saga.setState("DRIVER_RESERVED");
        }
        saga.setUpdatedAt(Instant.now());
        sagaRepository.save(saga);
        return Map.of("sagaId", sagaId, "tripId", saga.getTripId(), "state", saga.getState());
    }

    @Transactional
    public Map<String, Object> compensate(String sagaId, String reason) {
        Saga saga = getSagaOrThrow(sagaId);
        saga.setState("CANCELLED");
        saga.setLastError(reason);
        saga.setUpdatedAt(Instant.now());
        sagaRepository.save(saga);
        // TODO: emit compensation events: driver_release, trip_cancelled (future step)
        return Map.of("sagaId", sagaId, "tripId", saga.getTripId(), "state", saga.getState());
    }

    @Transactional(readOnly = true)
    public Map<String, Object> get(String sagaId) {
        Saga saga = getSagaOrThrow(sagaId);
        return Map.of(
                "sagaId", saga.getSagaId(),
                "tripId", saga.getTripId(),
                "driverId", saga.getDriverId(),
                "state", saga.getState(),
                "updatedAt", saga.getUpdatedAt(),
                "lastError", saga.getLastError()
        );
    }

    private Saga getSagaOrThrow(String sagaId) {
        Optional<Saga> opt = sagaRepository.findById(sagaId);
        return opt.orElseThrow(() -> new IllegalArgumentException("Saga not found: " + sagaId));
    }
}
