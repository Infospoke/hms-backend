package com.hms.service.config;

import com.hms.service.dto.NotificationEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationConsumerConfig {

    @KafkaListener(topics = "${hms.kafka.notification-topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(NotificationEvent event) {
        System.out.println("Received event: " + event);
    }
}
