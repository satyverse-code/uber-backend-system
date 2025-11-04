package com.uber.notification.kafka;

import com.uber.notification.store.NotificationEvent;
import com.uber.notification.store.NotificationEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import com.uber.notification.tracing.KafkaTracing;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.context.Scope;

@Component
public class NotificationListeners {

    private static final Logger log = LoggerFactory.getLogger(NotificationListeners.class);
    private final NotificationEventRepository repo;

    public NotificationListeners(NotificationEventRepository repo) {
        this.repo = repo;
    }

    @KafkaListener(topics = {"trip_created", "driver_assigned", "trip_completed", "payment_completed"},
            groupId = "notification-service-group")
    public void handle(Object event, ConsumerRecord<String, Object> record) {
        Span span = KafkaTracing.startConsumerSpan("notification.consume", record.headers());
        try (Scope scope = span.makeCurrent()) {
            // In real life, map event payload to notification target(s)
            String eventId = String.valueOf(event.hashCode()); // placeholder; expect event has eventId field
            NotificationEvent ne = repo.findById(eventId).orElse(new NotificationEvent(eventId, "generic", "rider:unknown", "RECEIVED"));
            try {
                // stub: log as send
                log.info("Sending notification for event: {}", event);
                ne.setStatus("SENT");
                repo.save(ne);
            } catch (Exception e) {
                ne.setStatus("FAILED");
                ne.setLastError(e.getMessage());
                repo.save(ne);
                throw e;
            }
        } finally {
            span.end();
        }
    }
}
