package com.hms.service.serviceImpl;

import com.hms.service.dto.NotificationEvent;
import com.hms.service.entity.NotificationEngineEntity;
import com.hms.service.repository.NotificationEngineRepository;
import com.hms.service.service.INotificationService;
import com.hms.service.service.IMailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class NotificationServiceImpl implements INotificationService {

    @Value("${hms.kafka.notification-topic}")
    private String notificationTopic;

    @Value("${hms.mail.from}")
    private String mailFrom;

    @Autowired
    private KafkaTemplate<String, NotificationEvent> kafkaTemplate;

    @Autowired
    private NotificationEngineRepository notificationEngineRepository;

    @Autowired
    private IMailService mailService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Override
    public void callNotification(NotificationEvent event) {
        log.info("Inside callNotification() for SR: {}", event.getSrId());
        publishToKafka(event);
    }

    //Publish to Kafka
    private void publishToKafka(NotificationEvent event) {
        log.info("Inside publishToKafka() for SR: {}", event.getSrId());
        try {
        	kafkaTemplate.send(notificationTopic, event.getSrId(), event);
			log.info("Notification Event published to Kafka for SR: {}", event.getSrId());
		} catch (Exception e) {
			log.error("Failed to publish event to Kafka for SR: {} - {}", event.getSrId(), e.getMessage());
        }

    }

    // Kafka Consumer — save to DB + send emails to all roles + push WebSocket
	@KafkaListener(topics = "${hms.kafka.notification-topic}", groupId = "${spring.kafka.consumer.group-id}", containerFactory = "kafkaListenerContainerFactory")
    public void consumeNotification(NotificationEvent event) {
        log.info("NotificationServiceImpl :: consumeNotification() - received event for SR: {}", event.getSrId());

        //Save notification to DB (one record per SR)
        saveNotification(event);

        // Send email to every user in the roleEmailMap
        sendEmailsToAllRoles(event);
        
        //Push real-time WebSocket notification to all subscribers
        pushWebSocketNotification(event);
        
        log.info("Notification fully processed for SR: {}", event.getSrId());
    }

    private void saveNotification(NotificationEvent event) {
    	log.info("Inside saveNotification() for SR: {}", event.getSrId());
        try {
            NotificationEngineEntity entity = new NotificationEngineEntity();
            entity.setNotificationTitle(event.getCheckerNotificationTitle());
            entity.setMessage(event.getMessage());
            entity.setSRId(event.getSrId());
            entity.setDeptName(event.getDeptName());
            entity.setRoleName(event.getJobTitle());
            entity.setNotificationSentAt(LocalDateTime.now());
            entity.setIsRead(false);

            notificationEngineRepository.save(entity);
            log.info("Notification saved to DB for SR: {}", event.getSrId());
        } catch (Exception e) {
            log.error("Failed to save notification for SR: {} - {}", event.getSrId(), e.getMessage());
        }
    }


    private void sendEmailsToAllRoles(NotificationEvent event) {
    	log.info("Inside sendEmailsToAllRoles() for SR: {}", event.getSrId());
        Map<Integer, List<String>> roleEmailMap = event.getRoleEmailMap();

        if (roleEmailMap == null || roleEmailMap.isEmpty()) {
            log.warn("roleEmailMap is empty for SR: {}. Skipping emails.", event.getSrId());
            return;
        }

        String subject = event.getCheckerNotificationTitle();
        String body=event.getEmailBody(); // Email body is sent from the producer which allows for more flexibility in email formatting.
        for (Map.Entry<Integer, List<String>> entry : roleEmailMap.entrySet()) {
            Integer roleId     = entry.getKey();
            List<String> emails = entry.getValue();

            if (emails == null || emails.isEmpty()) {
                log.warn("No emails for roleId: {} in SR: {}", roleId, event.getSrId());
                continue;
            }

            for (String email : emails) {
                try {
                    mailService.sendMail(mailFrom, email, null, subject, body, null);
                    log.info("Email sent to [{}] (roleId: {}) for SR: {}", email, roleId, event.getSrId());
                } catch (Exception e) {
                    log.error("Failed to send email to [{}] (roleId: {}) for SR: {} - {}",
                            email, roleId, event.getSrId(), e.getMessage());
                }
            }
        }
    }

    private void pushWebSocketNotification(NotificationEvent event) {
    	log.info("Inside pushWebSocketNotification() for SR: {}", event.getSrId());
        try {
            // Frontend subscribes to: /topic/notifications
            messagingTemplate.convertAndSend("/topic/notifications", event);
            log.info("NotificationServiceImpl :: WebSocket notification pushed for SR: {}", event.getSrId());
        } catch (Exception e) {
            log.error("NotificationServiceImpl :: Failed to push WebSocket notification for SR: {} - {}", event.getSrId(), e.getMessage());
        }
    }

}
