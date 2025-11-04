package com.uber.backend.kafka;

import com.uber.backend.events.TripCreatedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class TripEventProducer {
    public static final String TOPIC_TRIP_CREATED = "trip_created";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public TripEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishTripCreated(TripCreatedEvent event) {
        kafkaTemplate.send(TOPIC_TRIP_CREATED, event.getEventId(), event);
    }
}
