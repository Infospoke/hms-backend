package com.hms.service.config;


import com.hms.service.dto.NotificationEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationProducerConfig {

//    private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;
//
//    @Value("${hms.kafka.notification-topic}")
//    private String topic;
//
//    public NotificationProducerConfig(KafkaTemplate<String, NotificationEvent> kafkaTemplate) {
//        this.kafkaTemplate = kafkaTemplate;
//    }
//
//    public void sendNotification(NotificationEvent event) {
//        kafkaTemplate.send(topic, event);
//    }
}