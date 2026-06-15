package com.hms.service.config;


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