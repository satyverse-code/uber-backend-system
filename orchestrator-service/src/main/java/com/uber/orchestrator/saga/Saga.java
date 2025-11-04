package com.uber.orchestrator.saga;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "sagas")
public class Saga {

    @Id
    @Column(name = "saga_id", length = 64)
    private String sagaId;

    @Column(name = "trip_id")
    private Long tripId;

    @Column(name = "driver_id")
    private Long driverId;

    @Column(nullable = false)
    private String state; // STARTED, DRIVER_RESERVED, PAYMENT_AUTHORIZED, COMMITTED, COMPENSATING, CANCELLED

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    @Column(name = "last_error")
    private String lastError;

    public Saga() {}

    public Saga(String sagaId, Long tripId, String state) {
        this.sagaId = sagaId;
        this.tripId = tripId;
        this.state = state;
    }

    public String getSagaId() { return sagaId; }
    public void setSagaId(String sagaId) { this.sagaId = sagaId; }
    public Long getTripId() { return tripId; }
    public void setTripId(Long tripId) { this.tripId = tripId; }
    public Long getDriverId() { return driverId; }
    public void setDriverId(Long driverId) { this.driverId = driverId; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
    public String getLastError() { return lastError; }
    public void setLastError(String lastError) { this.lastError = lastError; }
}
