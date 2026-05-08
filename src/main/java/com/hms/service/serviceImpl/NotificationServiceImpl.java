package com.hms.service.serviceImpl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.hms.service.dto.NotificationEvent;
import com.hms.service.entity.NotificationEngineEntity;
import com.hms.service.repository.NotificationEngineRepository;
import com.hms.service.request.SpecificationFilterRequest;
import com.hms.service.request.UpdateNotificationRequest;
import com.hms.service.service.IMailService;
import com.hms.service.service.INotificationService;
import com.hms.service.wrappers.ApiResponse;
import com.hms.service.wrappers.ResponseCode;

import lombok.extern.slf4j.Slf4j;

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
        kafkaTemplate.send(notificationTopic, event.getSrId(), event);
        log.info("NotificationServiceImpl :: Event published to Kafka for SR: {}", event.getSrId());
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

        log.info("NotificationServiceImpl :: Notification fully processed for SR: {}", event.getSrId());
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
            log.info("NotificationServiceImpl :: Notification saved to DB for SR: {}", event.getSrId());
        } catch (Exception e) {
            log.error("NotificationServiceImpl :: Failed to save notification for SR: {} - {}", event.getSrId(), e.getMessage());
        }
    }


    private void sendEmailsToAllRoles(NotificationEvent event) {
    	log.info("Inside sendEmailsToAllRoles() for SR: {}", event.getSrId());
        Map<Integer, List<String>> roleEmailMap = event.getRoleEmailMap();

        if (roleEmailMap == null || roleEmailMap.isEmpty()) {
            log.warn("NotificationServiceImpl :: roleEmailMap is empty for SR: {}. Skipping emails.", event.getSrId());
            return;
        }

        String subject = event.getCheckerNotificationTitle();
        String body=event.getEmailBody(); // Email body is sent from the producer which allows for more flexibility in email formatting.
        for (Map.Entry<Integer, List<String>> entry : roleEmailMap.entrySet()) {
            Integer roleId     = entry.getKey();
            List<String> emails = entry.getValue();

            if (emails == null || emails.isEmpty()) {
                log.warn("NotificationServiceImpl :: No emails for roleId: {} in SR: {}", roleId, event.getSrId());
                continue;
            }

            for (String email : emails) {
                try {
                    mailService.sendMail(mailFrom, email, null, subject, body, null);
                    log.info("NotificationServiceImpl :: Email sent to [{}] (roleId: {}) for SR: {}", email, roleId, event.getSrId());
                } catch (Exception e) {
                    log.error("NotificationServiceImpl :: Failed to send email to [{}] (roleId: {}) for SR: {} - {}",
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

    
    //give buildEmailBody as constant string with placeholders and replace placeholders with actual values from event object  
    
//    private String buildEmailBody(NotificationEvent event) {
//        return "<html><body>"
//                + "<h3>" + event.getCheckerNotificationTitle() + "</h3>"
//                + "<p>" + event.getMessage() + "</p>"
//                + "<br/>"
//                + "<b>SR ID:</b> " + event.getSrId() + "<br/>"
//                + "<b>Job Title:</b> " + event.getJobTitle() + "<br/>"
//                + "<b>Department:</b> " + event.getDeptName() + "<br/>"
//                + "<b>Submitted At:</b> " + event.getTriggeredAt() + "<br/>"
//                + "</body></html>";
//    }
    
  
    @Override
    public ApiResponse<?> getNotifications(SpecificationFilterRequest request) {

        log.info("NotificationServiceImpl:: Inside getNotifications");

        if (request.getPage() == null || request.getSize() == null) {

            return ApiResponse.failure(
                    ResponseCode.FAILURE,
                    "failure",
                    List.of("page and size must be provided")
            );
        }

        if (request.getPage() < 0 || request.getSize() <= 0) {

            return ApiResponse.failure(
                    ResponseCode.FAILURE,
                    "failure",
                    List.of("Invalid page or size values")
            );
        }

        Sort sort = Sort.by(
                "DESC".equalsIgnoreCase(request.getDirection())
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC,

                request.getSortBy() != null
                        ? request.getSortBy()
                        : "notificationSentAt"
        );

        Pageable pageable = PageRequest.of(
                request.getPage(),
                request.getSize(),
                sort
        );

        Page<NotificationEngineEntity> pageResult =
                notificationEngineRepository.findAll(
                        request.toNotificationSpecification(),
                        pageable
                );

        Map<String, Object> response = new HashMap<>();

        response.put("notifications", pageResult.getContent());
        response.put("currentPage", pageResult.getNumber());
        response.put("totalPages", pageResult.getTotalPages());
        response.put("totalElements", pageResult.getTotalElements());

        log.info("NotificationServiceImpl:: Exit getNotifications");

        return ApiResponse.success(
                ResponseCode.SUCCESS,
                "success",
                response
        );
    }
    @Override
    public ApiResponse<?> getNotificationCounts() {

        log.info("NotificationServiceImpl:: Inside getNotificationCounts");

        Long total = notificationEngineRepository.count();
        Long read = notificationEngineRepository.countByIsRead(true);
        Long unread = notificationEngineRepository.countByIsRead(false);

        Map<String, Object> response = new HashMap<>();
        response.put("total", total);
        response.put("read", read);
        response.put("unread", unread);

        log.info("NotificationServiceImpl:: Exit getNotificationCounts");

        return ApiResponse.success(ResponseCode.SUCCESS, "success", response);
    }

	@Override
	public ApiResponse<?> updateNotifications(UpdateNotificationRequest request) {

		log.info("NotificationServiceImpl::Inside updateNotifications (Batch)");

		if (request.getIds() == null || request.getIds().isEmpty()) {
			return ApiResponse.failure(ResponseCode.FAILURE, "Notification IDs are required");
		}

		List<NotificationEngineEntity> notificationEngineEntity = notificationEngineRepository
				.findAllById(request.getIds());

		if (notificationEngineEntity.isEmpty()) {
			return ApiResponse.failure(ResponseCode.FAILURE, "No notifications found");
		}

		for (NotificationEngineEntity entity : notificationEngineEntity) {
			entity.setIsRead(request.getIsRead());
		}

		notificationEngineRepository.saveAll(notificationEngineEntity);

		return ApiResponse.success("Notifications updated successfully");
	}

    
    
}
