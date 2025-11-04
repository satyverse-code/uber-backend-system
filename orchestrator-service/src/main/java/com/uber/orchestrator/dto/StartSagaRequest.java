package com.uber.orchestrator.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class StartSagaRequest {
    @NotNull
    private Long riderId;
    @NotBlank
    private String pickup;
    @NotBlank
    private String dropoff;
    @NotNull
    private Double lat;
    @NotNull
    private Double lng;

    public Long getRiderId() { return riderId; }
    public void setRiderId(Long riderId) { this.riderId = riderId; }
    public String getPickup() { return pickup; }
    public void setPickup(String pickup) { this.pickup = pickup; }
    public String getDropoff() { return dropoff; }
    public void setDropoff(String dropoff) { this.dropoff = dropoff; }
    public Double getLat() { return lat; }
    public void setLat(Double lat) { this.lat = lat; }
    public Double getLng() { return lng; }
    public void setLng(Double lng) { this.lng = lng; }
}
