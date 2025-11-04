package com.uber.backend.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TopicConfig {

    @Bean
    public NewTopic tripCreatedTopic() {
        return new NewTopic("trip_created", 3, (short) 1);
    }

    @Bean
    public NewTopic driverAssignedTopic() {
        return new NewTopic("driver_assigned", 3, (short) 1);
    }

    @Bean
    public NewTopic paymentCompletedTopic() {
        return new NewTopic("payment_completed", 3, (short) 1);
    }

    @Bean
    public NewTopic tripCompletedTopic() {
        return new NewTopic("trip_completed", 3, (short) 1);
    }
}
