package com.uber.backend.driver;

public interface DriverNearby {
    Long getId();
    String getName();
    String getCarNumber();
    String getStatus();
    Double getDistance();
}
