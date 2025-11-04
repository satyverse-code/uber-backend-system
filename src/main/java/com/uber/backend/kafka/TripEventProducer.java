package com.uber.backend.kafka;

import com.uber.backend.events.TripCreatedEvent;
import com.uber.backend.analytics.S3Exporter;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class TripEventProducer {
    public static final String TOPIC_TRIP_CREATED = "trip_created";

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final S3Exporter s3Exporter;

    public TripEventProducer(KafkaTemplate<String, Object> kafkaTemplate, S3Exporter s3Exporter) {
        this.kafkaTemplate = kafkaTemplate;
        this.s3Exporter = s3Exporter;
    }

    public void publishTripCreated(TripCreatedEvent event) {
        kafkaTemplate.send(TOPIC_TRIP_CREATED, event.getEventId(), event);
        s3Exporter.export(event);
    }
}
