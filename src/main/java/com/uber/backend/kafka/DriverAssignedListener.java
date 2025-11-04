package com.uber.backend.kafka;

import com.uber.backend.events.DriverAssignedEvent;
import com.uber.backend.service.RideService;
import com.uber.backend.tracing.KafkaTracing;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.time.Instant;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.context.Scope;

@Component
public class DriverAssignedListener {

    private final RideService rideService;

    public DriverAssignedListener(RideService rideService) {
        this.rideService = rideService;
    }

    @KafkaListener(topics = "driver_assigned", groupId = "booking-service-group")
    public void handle(DriverAssignedEvent event,
                       @Header(name = KafkaHeaders.RECEIVED_MESSAGE_KEY, required = false) String key,
                       ConsumerRecord<String, DriverAssignedEvent> record) {
        Span span = KafkaTracing.startConsumerSpan("driver_assigned.consume", record.headers());
        try (Scope scope = span.makeCurrent()) {
            Instant assignedAt = event.getAssignedAt() != null ? event.getAssignedAt() : Instant.now();
            rideService.assignDriver(event.getTripId(), event.getDriverId(), assignedAt, event.getEventId());
        } finally {
            span.end();
        }
    }
}
