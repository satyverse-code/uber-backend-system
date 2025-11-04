package com.uber.backend.controller;

import com.uber.backend.dto.RideRequest;
import com.uber.backend.dto.RideResponse;
import com.uber.backend.model.Ride;
import com.uber.backend.service.RideService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rides")
public class RideController {

    private final RideService rideService;

    public RideController(RideService rideService) {
        this.rideService = rideService;
    }

    @PostMapping
    public ResponseEntity<RideResponse> createRide(@RequestBody RideRequest request) {
        RideResponse response = rideService.createRide(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/rider/{riderId}")
    public ResponseEntity<List<Ride>> getRides(@PathVariable Long riderId) {
        return ResponseEntity.ok(rideService.getRidesForRider(riderId));
    }
}
