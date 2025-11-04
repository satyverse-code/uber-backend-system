package com.uber.backend.kafka;

import com.uber.backend.events.TripCreatedEvent;
import com.uber.backend.tracing.KafkaTracing;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.apache.kafka.clients.producer.ProducerRecord;

@Service
public class TripEventProducer {
    public static final String TOPIC_TRIP_CREATED = "trip_created";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public TripEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishTripCreated(TripCreatedEvent event) {
        ProducerRecord<String, Object> record = new ProducerRecord<>(TOPIC_TRIP_CREATED, event.getEventId(), event);
        KafkaTracing.inject(record);
        kafkaTemplate.send(record);
    }
}
