package com.hms.service.serviceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.hms.service.dto.NotificationEvent;
import com.hms.service.dto.WebSocketNotification;
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
    	
        log.info("Inside callNotification() for"+ event.getType()+": {}", event.getProcessId());
        log.info("The event into the kafka : "+event);
        publishToKafka(event);
    }

    //Publish to Kafka 
    private void publishToKafka(NotificationEvent event) {
        log.info("Inside publishToKafka() for"+ event.getType()+": {}", event.getProcessId());
        kafkaTemplate.send(notificationTopic, event.getProcessId(), event);
        log.info("NotificationServiceImpl :: Event published to Kafka for "+ event.getType()+": {}", event.getProcessId());
    }

    // Kafka Consumer — save to DB + send emails to all roles + push WebSocket
	@KafkaListener(topics = "${hms.kafka.notification-topic}", groupId = "${spring.kafka.consumer.group-id}", containerFactory = "kafkaListenerContainerFactory")
    public void consumeNotification(NotificationEvent event) {
        log.info("NotificationServiceImpl :: consumeNotification() - received event for "+ event.getType()+": {}", event.getProcessId());

        //Save notification to DB (one record per SR)
        saveNotification(event);

        // Send email to every user in the roleEmailMap
        sendEmailsToAllRoles(event);
        
        //Push real-time WebSocket notification to all subscribers
        pushWebSocketNotification(event);

        log.info("NotificationServiceImpl :: Notification fully processed for "+ event.getType()+": {}", event.getProcessId());
    }


    private void saveNotification(NotificationEvent event) {
    	log.info("Inside saveNotification() for "+ event.getType()+": {}", event.getProcessId());
    	List<NotificationEngineEntity> notificationsList = new ArrayList<>();
    	
        try {
            NotificationEngineEntity checkerEntity = new NotificationEngineEntity();
            checkerEntity.setNotificationTitle(event.getCheckerNotificationTitle());
            checkerEntity.setMessage(event.getMessage());
            checkerEntity.setProcessId(event.getProcessId());
            checkerEntity.setDeptName(event.getDeptName());
            checkerEntity.setRoleName(event.getCheckerRoleName());
            checkerEntity.setNotificationSentAt(LocalDateTime.now());
            checkerEntity.setIsRead(false);
            
            NotificationEngineEntity makerEntity = new NotificationEngineEntity();
            makerEntity.setNotificationTitle(event.getMakerNotificationTitle());
            log.info("The event message to the Checker is : "+event.getMessage());
            makerEntity.setMessage(event.getMessage());
            makerEntity.setProcessId(event.getProcessId());
            makerEntity.setDeptName(event.getDeptName());
            makerEntity.setRoleName(event.getMakerRoleName());
         
            makerEntity.setNotificationSentAt(LocalDateTime.now());
            makerEntity.setIsRead(false);
            
            notificationsList.add(checkerEntity);
            notificationsList.add(makerEntity);
            
            notificationEngineRepository.saveAll(notificationsList);
            log.info("NotificationServiceImpl :: Notifications saved to DB for "+ event.getType()+": {}", event.getProcessId());
        } catch (Exception e) {
            log.error("NotificationServiceImpl :: Failed to save notification for "+ event.getType()+": {} - {}", event.getProcessId(), e.getMessage());
        }
    }


    private void sendEmailsToAllRoles(NotificationEvent event) {
    	log.info("Inside sendEmailsToAllRoles() for "+ event.getType()+": {}", event.getProcessId());
        Map<Integer, List<String>> roleEmailMap = event.getRoleEmailMap();

        if (roleEmailMap == null || roleEmailMap.isEmpty()) {
            log.warn("NotificationServiceImpl :: roleEmailMap is empty for "+ event.getType()+": {}. Skipping emails.", event.getProcessId());
            return;
        }
        String checkerSubject = event.getCheckerNotificationTitle();
        String checkerBody=event.getCheckerEmailBody(); // Email body is sent from the producer which allows for more flexibility in email formatting.
        String makerSubject = event.getMakerNotificationTitle();
        String makerBody=event.getMakerEmailBody();
        
        for (Map.Entry<Integer, List<String>> entry : roleEmailMap.entrySet()) {
            Integer roleId     = entry.getKey();
            List<String> checkerEmails = entry.getValue();

            if (checkerEmails == null || checkerEmails.isEmpty()) {
                log.warn("NotificationServiceImpl :: No emails for roleId: {} in "+ event.getType()+": {}", roleId, event.getProcessId());
                continue;
            }
           
            //Sending email to maker
            mailService.sendMail(mailFrom, event.getMakerEmailAddress(), null, makerSubject, makerBody, null);
            for (String email : checkerEmails) {
                try {
                	//Sending email to checker
                    mailService.sendMail(mailFrom, email, null, checkerSubject, checkerBody, null);
                    log.info("NotificationServiceImpl :: Email sent to [{}] (roleId: {}) for "+ event.getType()+": {}", email, roleId, event.getProcessId());
                } catch (Exception e) {
                    log.error("NotificationServiceImpl :: Failed to send email to [{}] (roleId: {}) for "+ event.getType()+": {} - {}",
                            email, roleId, event.getProcessId(), e.getMessage());
                }
            }
        }
    }

    private void pushWebSocketNotification(NotificationEvent event) {
        log.info("Inside pushWebSocketNotification() for {}: {}", event.getType(), event.getProcessId());
        try {
            if (event.getMakerRoleId() != null) {
                WebSocketNotification makerNotif = new WebSocketNotification(
                        event.getProcessId(),
                        event.getMakerNotificationTitle(),
                        event.getMakerEmailBody(),
                        event.getDeptName(),
                        "MAKER",
                        event.getMakerRoleId()
                );
                messagingTemplate.convertAndSend("/topic/notifications/" + event.getMakerRoleId(), makerNotif);
                log.info("NotificationServiceImpl :: WebSocket pushed to maker roleId: {}", event.getMakerRoleId());
            }

            if (event.getRoleEmailMap() != null) {
                for (Integer checkerRoleId : event.getRoleEmailMap().keySet()) {
                    WebSocketNotification checkerNotif = new WebSocketNotification(
                            event.getProcessId(),
                            event.getCheckerNotificationTitle(),
                            event.getMessage(),
                            event.getDeptName(),
                            "CHECKER",
                            checkerRoleId
                    );
                    messagingTemplate.convertAndSend("/topic/notifications/" + checkerRoleId, checkerNotif);
                    log.info("NotificationServiceImpl :: WebSocket pushed to checker roleId: {}", checkerRoleId);
                }
            }

            log.info("NotificationServiceImpl :: WebSocket notification fully pushed for {}: {}", event.getType(), event.getProcessId());
        } catch (Exception e) {
            log.error("NotificationServiceImpl :: Failed to push WebSocket notification for {}: {} - {}", event.getType(), event.getProcessId(), e.getMessage());
        }
    }
     
    @Override
    public ApiResponse<?> getNotifications(SpecificationFilterRequest request) {

        log.info("NotificationServiceImpl:: Inside getNotifications");

        if (request.getPage() == null || request.getSize() == null) {

            return ApiResponse.failure(
                    ResponseCode.FAILURE,                    "failure",
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
     // LIST FILTERS
        Specification<NotificationEngineEntity> baseSpec =
                request.toNotificationSpecification();

        Page<NotificationEngineEntity> pageResult =
                notificationEngineRepository.findAll(
                        baseSpec,
                        pageable
                );

        // COUNT FILTERS
        Specification<NotificationEngineEntity> countSpec =
                request.buildNotificationCountSpec();

        long totalCount =
                notificationEngineRepository.count(countSpec);

        long readCount =
                notificationEngineRepository.count(
                        countSpec.and(isReadEquals(true))
                );

        long unreadCount =
                notificationEngineRepository.count(
                        countSpec.and(isReadEquals(false))
                );

        Map<String, Object> counts = new LinkedHashMap<>();

        counts.put("total", totalCount);
        counts.put("read", readCount);
        counts.put("unread", unreadCount);

        Map<String, Object> response = new LinkedHashMap<>();

        response.put("notifications", pageResult.getContent());

        response.put("currentPage", pageResult.getNumber());

        response.put("totalPages", pageResult.getTotalPages());

        response.put("totalElements", pageResult.getTotalElements());

        response.put("counts", counts);

        log.info("NotificationServiceImpl:: Exit getNotifications");

        return ApiResponse.success(
                ResponseCode.SUCCESS,
                "success",
                response
        );
    }

    private Specification<NotificationEngineEntity> isReadEquals(
            Boolean value) {

        return (r, q, c) ->
                c.equal(r.get("isRead"), value);
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
		
		log.info("the notification ids are:"+request.getIds());

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

