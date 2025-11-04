package com.uber.backend.events;

import java.time.Instant;

public class TripCreatedEvent {
    private String eventId;
    private Long tripId;
    private Long riderId;
    private String pickupLocation;
    private String dropoffLocation;
    private Instant createdAt;

    public TripCreatedEvent() {}

    public TripCreatedEvent(String eventId, Long tripId, Long riderId, String pickupLocation, String dropoffLocation, Instant createdAt) {
        this.eventId = eventId;
        this.tripId = tripId;
        this.riderId = riderId;
        this.pickupLocation = pickupLocation;
        this.dropoffLocation = dropoffLocation;
        this.createdAt = createdAt;
    }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    public Long getTripId() { return tripId; }
    public void setTripId(Long tripId) { this.tripId = tripId; }
    public Long getRiderId() { return riderId; }
    public void setRiderId(Long riderId) { this.riderId = riderId; }
    public String getPickupLocation() { return pickupLocation; }
    public void setPickupLocation(String pickupLocation) { this.pickupLocation = pickupLocation; }
    public String getDropoffLocation() { return dropoffLocation; }
    public void setDropoffLocation(String dropoffLocation) { this.dropoffLocation = dropoffLocation; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
