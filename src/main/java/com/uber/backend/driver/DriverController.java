package com.uber.backend.driver;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/drivers")
public class DriverController {

    private final DriverService driverService;

    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    @PostMapping("/reserve")
    public ResponseEntity<?> reserve(@RequestBody ReserveRequest req) {
        double radius = req.radiusMeters() == null ? 5000.0 : req.radiusMeters();
        DriverService.ReservedDriver rd = driverService.reserveNearest(req.lat(), req.lng(), radius);
        if (!rd.found()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(Map.of(
                "driverId", rd.id(),
                "name", rd.name(),
                "carNumber", rd.carNumber()
        ));
    }

    public record ReserveRequest(double lat, double lng, Double radiusMeters) {}
}
