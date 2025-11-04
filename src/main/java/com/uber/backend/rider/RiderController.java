package com.uber.backend.rider;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/riders")
public class RiderController {

    private final RiderRepository riderRepository;

    public RiderController(RiderRepository riderRepository) {
        this.riderRepository = riderRepository;
    }

    @GetMapping
    public List<Rider> list() {
        return riderRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Rider> get(@PathVariable Long id) {
        return riderRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
