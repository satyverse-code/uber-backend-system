package com.uber.backend.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class BookingMetrics {

    private final Counter rideRequested;
    private final Counter rideAssigned;
    private final Counter rideFailed;
    private final Timer assignmentLatency;

    public BookingMetrics(MeterRegistry registry) {
        this.rideRequested = Counter.builder("ride.requested.count")
                .description("Number of ride requests created")
                .register(registry);
        this.rideAssigned = Counter.builder("ride.assigned.count")
                .description("Number of rides assigned to drivers")
                .register(registry);
        this.rideFailed = Counter.builder("ride.failed.count")
                .description("Number of failed ride assignments or processing errors")
                .register(registry);
        this.assignmentLatency = Timer.builder("ride.assignment.latency")
                .description("Latency from ride requested to driver assigned")
                .publishPercentileHistogram()
                .register(registry);
    }

    public void incRequested() { rideRequested.increment(); }
    public void incAssigned() { rideAssigned.increment(); }
    public void incFailed() { rideFailed.increment(); }

    public void recordAssignmentLatency(Duration d) {
        if (d != null && !d.isNegative()) {
            assignmentLatency.record(d);
        }
    }
}
