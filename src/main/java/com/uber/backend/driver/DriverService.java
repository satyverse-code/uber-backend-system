package com.uber.backend.driver;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DriverService {

    private final DriverRepository driverRepository;
    private final DriverCacheService driverCacheService;

    public DriverService(DriverRepository driverRepository, DriverCacheService driverCacheService) {
        this.driverRepository = driverRepository;
        this.driverCacheService = driverCacheService;
    }

    @Transactional
    public ReservedDriver reserveNearest(double lat, double lng, double radiusMeters) {
        List<DriverNearby> candidates = driverRepository.findNearestWithin(lat, lng, radiusMeters, 1);
        if (candidates.isEmpty()) {
            return ReservedDriver.none();
        }
        Long id = candidates.get(0).getId();
        Driver driver = driverRepository.findById(id).orElseThrow();
        if (!"AVAILABLE".equalsIgnoreCase(driver.getStatus())) {
            return ReservedDriver.none();
        }
        driver.setStatus("BUSY");
        driverRepository.save(driver);
        driverCacheService.putDriverSnapshot(driver.getId(), driver.getStatus(), driver.getCurrentLat(), driver.getCurrentLong());
        return new ReservedDriver(driver.getId(), driver.getName(), driver.getCarNumber());
    }

    public record ReservedDriver(Long id, String name, String carNumber) {
        static ReservedDriver none() { return new ReservedDriver(null, null, null); }
        public boolean found() { return id != null; }
    }
}
