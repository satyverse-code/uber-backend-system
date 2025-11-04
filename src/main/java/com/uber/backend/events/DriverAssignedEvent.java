package com.uber.backend.events;

import java.time.Instant;

public class DriverAssignedEvent {
    private String eventId;
    private Long tripId;
    private Long driverId;
    private Instant assignedAt;

    public DriverAssignedEvent() {}

    public DriverAssignedEvent(String eventId, Long tripId, Long driverId, Instant assignedAt) {
        this.eventId = eventId;
        this.tripId = tripId;
        this.driverId = driverId;
        this.assignedAt = assignedAt;
    }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    public Long getTripId() { return tripId; }
    public void setTripId(Long tripId) { this.tripId = tripId; }
    public Long getDriverId() { return driverId; }
    public void setDriverId(Long driverId) { this.driverId = driverId; }
    public Instant getAssignedAt() { return assignedAt; }
    public void setAssignedAt(Instant assignedAt) { this.assignedAt = assignedAt; }
}
