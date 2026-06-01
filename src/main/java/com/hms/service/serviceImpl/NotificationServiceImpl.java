package com.hms.service.serviceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.hms.service.dto.NotificationEvent;
import com.hms.service.dto.WebSocketNotification;
import com.hms.service.entity.AssignRolesEntity;
import com.hms.service.entity.NotificationEngineEntity;
import com.hms.service.repository.AssignRolesRepository;
import com.hms.service.repository.NotificationEngineRepository;

import com.hms.service.request.SpecificationFilterRequest;
import com.hms.service.request.UpdateNotificationRequest;
import com.hms.service.service.IMailService;
import com.hms.service.service.INotificationService;
import com.hms.service.utils.JwtService;
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
	private AssignRolesRepository assignRolesRepository;

	@Autowired
	private IMailService mailService;

	@Autowired
	private SimpMessagingTemplate messagingTemplate;

	@Autowired
	private HttpServletRequest httpServletRequest;

	@Autowired
	private JwtService jwtService;

	@Override
	public void callNotification(NotificationEvent event) {

		log.info("Inside callNotification() for" + event.getType() + ": {}", event.getProcessId());
		log.info("The event into the kafka : " + event);
		publishToKafka(event);
	}

	// Publish to Kafka
	private void publishToKafka(NotificationEvent event) {
		log.info("Inside publishToKafka() for" + event.getType() + ": {}", event.getProcessId());
		kafkaTemplate.send(notificationTopic, event.getProcessId(), event);
		log.info("NotificationServiceImpl :: Event published to Kafka for " + event.getType() + ": {}",
				event.getProcessId());
	}

	// Kafka Consumer — save to DB + send emails to all roles + push WebSocket
	@KafkaListener(topics = "${hms.kafka.notification-topic}", groupId = "${spring.kafka.consumer.group-id}", containerFactory = "kafkaListenerContainerFactory")
	public void consumeNotification(NotificationEvent event, Acknowledgment acknowledgment) {
		log.info("NotificationServiceImpl :: consumeNotification() - received event for " + event.getType() + ": {}",
				event.getProcessId());

		// Save notification to DB (one record per SR)
		saveNotification(event, acknowledgment);
		
		log.info("the event from the notification service impl is : "+event);

		List<Object[]> results = notificationEngineRepository.findIdAndSentAtByProcessIdAndSentIsFalse(event.getProcessId());
		
		log.info("event contains the maker role id is"+event.getMakerRoleId());
		if (event.getMakerRoleId() != null) {
	     	Object[] makerRow = results.get(1);
			Integer makerNotificationId = (Integer) makerRow[0];
			log.info("maker notification is is"+makerNotificationId);
			LocalDateTime makerNotificationSentAt = (LocalDateTime) makerRow[1];
			event.setTriggeredAt(makerNotificationSentAt);
			
			event.setMakerId(makerNotificationId);
			log.info("maker id is"+makerNotificationId);
		}
		if (event.getRoleEmailMap() != null && results.size() > 1) {

			Object[] checkerRow = results.get(0);
			LocalDateTime checkerNotificationSentAt = (LocalDateTime) checkerRow[1];

			event.setTriggeredAt(checkerNotificationSentAt);
			Integer checkerNotificationId = (Integer) checkerRow[0];
			event.setCheckerId(checkerNotificationId);
		}

		// Push real-time WebSocket notification to all subscribers
		pushWebSocketNotification(event);
		
		updateNotification(event);

		// Send email to every user in the roleEmailMap
		sendEmailsToAllRoles(event);
		log.info("NotificationServiceImpl :: Notification fully processed for " + event.getType() + ": {}",
				event.getProcessId());
	}

	private void saveNotification(NotificationEvent event, Acknowledgment acknowledgment) {
		log.info("Inside saveNotification() for " + event.getType() + ": {}", event.getProcessId());
		List<NotificationEngineEntity> notificationsList = new ArrayList<>();

		try {
			NotificationEngineEntity checkerEntity = new NotificationEngineEntity();
			checkerEntity.setNotificationTitle(event.getCheckerNotificationTitle());

			checkerEntity.setMessage(event.getCheckerMessage());

			checkerEntity.setProcessId(event.getProcessId());
			checkerEntity.setDeptName(event.getDeptName());
			checkerEntity.setRoleName(event.getCheckerRoleName());
			
			log.info("checker role name is"+event.getCheckerRoleName());
			checkerEntity.setNotificationSentAt(LocalDateTime.now());
			Integer checkerRoleId = event.getRoleEmailMap().keySet().stream().findFirst().orElse(null); 
			checkerEntity.setRoleId(checkerRoleId);
			checkerEntity.setIsRead(false);

			NotificationEngineEntity makerEntity = new NotificationEngineEntity();
			makerEntity.setNotificationTitle(event.getMakerNotificationTitle());

			log.info("The event message to the Checker is : " + event.getMakerMessage());
			makerEntity.setMessage(event.getMakerMessage());

			makerEntity.setProcessId(event.getProcessId());
			makerEntity.setDeptName(event.getDeptName());
			makerEntity.setRoleName(event.getMakerRoleName());
			log.info("maker role name is "+event.getMakerRoleName());
			
			makerEntity.setRoleId(event.getMakerRoleId());

			makerEntity.setNotificationSentAt(LocalDateTime.now());
			makerEntity.setIsRead(false);

			notificationsList.add(checkerEntity);
			notificationsList.add(makerEntity);

			notificationEngineRepository.saveAll(notificationsList);
			log.info("NotificationServiceImpl :: Notifications saved to DB for " + event.getType() + ": {}",
					event.getProcessId());

			acknowledgment.acknowledge(); // Manually acknowledge after successful DB save

		} catch (Exception e) {
			log.error("NotificationServiceImpl :: Failed to save notification for " + event.getType() + ": {} - {}",
					event.getProcessId(), e.getMessage());
		}
	}
	
	
	private void updateNotification(NotificationEvent event) {
		log.info("Inside update Notifications for " + event.getType() + ": {}", event.getProcessId());
		
		List<NotificationEngineEntity> notifications= notificationEngineRepository.findByProcessIdAndSentIsFalse(event.getProcessId());
		List<NotificationEngineEntity> notificationsList = new ArrayList<>();
		for(int i=0;i<notificationsList.size();i++)
		{
			NotificationEngineEntity checkerEntity = notifications.get(i);
			checkerEntity.setSent(true);
			notificationsList.add(checkerEntity);
		}
		notificationEngineRepository.saveAll(notifications);
		
		
		log.info("Successfully updated Notifications for " + event.getType() + ": {}", event.getProcessId());

	}

	private void sendEmailsToAllRoles(NotificationEvent event) {
		log.info("Inside sendEmailsToAllRoles() for " + event.getType() + ": {}", event.getProcessId());
		Map<Integer, List<String>> roleEmailMap = event.getRoleEmailMap();

		if (roleEmailMap == null || roleEmailMap.isEmpty()) {
			log.warn("NotificationServiceImpl :: roleEmailMap is empty for " + event.getType()
					+ ": {}. Skipping emails.", event.getProcessId());
			return;
		}
		String checkerSubject = event.getCheckerNotificationTitle();
		String checkerBody = event.getCheckerEmailBody(); // Email body is sent from the producer which allows for more
															// flexibility in email formatting.
		String makerSubject = event.getMakerNotificationTitle();
		String makerBody = event.getMakerEmailBody();

		for (Map.Entry<Integer, List<String>> entry : roleEmailMap.entrySet()) {
			Integer roleId = entry.getKey();
			List<String> checkerEmails = entry.getValue();

			if (checkerEmails == null || checkerEmails.isEmpty()) {
				log.warn("NotificationServiceImpl :: No emails for roleId: {} in " + event.getType() + ": {}", roleId,
						event.getProcessId());
				continue;
			}

			// Sending email to maker
			mailService.sendMail(mailFrom, event.getMakerEmailAddress(), null, makerSubject, makerBody, null);
			for (String email : checkerEmails) {
				try {
					// Sending email to checker
					mailService.sendMail(mailFrom, email, null, checkerSubject, checkerBody, null);
					log.info("NotificationServiceImpl :: Email sent to [{}] (roleId: {}) for " + event.getType()
							+ ": {}", email, roleId, event.getProcessId());
				} catch (Exception e) {
					log.error(
							"NotificationServiceImpl :: Failed to send email to [{}] (roleId: {}) for "
									+ event.getType() + ": {} - {}",
							email, roleId, event.getProcessId(), e.getMessage());
				}
			}
		}
	}

	private void pushWebSocketNotification(NotificationEvent event) {
		log.info("Inside pushWebSocketNotification() for {}: {}", event.getType(), event.getProcessId());
		try {
			if (event.getMakerRoleId() != null) {
				WebSocketNotification makerNotification = new WebSocketNotification(event.getProcessId(),
						event.getMakerNotificationTitle(), event.getMakerMessage(), event.getDeptName(),

						event.getType(),

						event.getMakerRoleId(), event.getTriggeredAt(), event.getMakerId());
				log.info("maker notification is "+makerNotification);
				messagingTemplate.convertAndSend("/topic/notifications/" + event.getMakerRoleId(), makerNotification);
				log.info("NotificationServiceImpl :: WebSocket pushed to maker roleId: {}", event.getMakerRoleId());
			}

			Integer checkerRoleId = event.getRoleEmailMap().keySet().stream().findFirst().orElse(null);

			if (event.getRoleEmailMap() != null) {
				// for (Integer checkerRoleId : event.getRoleEmailMap().keySet()) {
				WebSocketNotification checkerNotification = new WebSocketNotification(event.getProcessId(),
						event.getCheckerNotificationTitle(), event.getCheckerMessage(), event.getDeptName(),
						event.getType(), checkerRoleId, event.getTriggeredAt(), event.getCheckerId());
				log.info("checker notification is"+checkerNotification);
				messagingTemplate.convertAndSend("/topic/notifications/" + checkerRoleId, checkerNotification);
				log.info("NotificationServiceImpl :: WebSocket pushed to checker roleId: {}", checkerRoleId);
				// }
			}

			log.info("NotificationServiceImpl :: WebSocket notification fully pushed for {}: {}", event.getType(),
					event.getProcessId());
		} catch (Exception e) {
			log.error("NotificationServiceImpl :: Failed to push WebSocket notification for {}: {} - {}",
					event.getType(), event.getProcessId(), e.getMessage());
		}
	}

	@Override
	public ApiResponse<?> getNotifications(SpecificationFilterRequest request) {

		String authHeader = httpServletRequest.getHeader("Authorization");

		Long userId = null;

		if (authHeader != null && authHeader.startsWith("Bearer ")) {

			String token = authHeader.substring(7);

			userId = jwtService.extractUserId(token);

		} else {

			return ApiResponse.failure(ResponseCode.FAILURE, "Unauthorized", List.of("Missing or invalid token"));
		}

		AssignRolesEntity assignRole = assignRolesRepository.findByUserId(userId.intValue())
				.orElseThrow(() -> new RuntimeException("Role not assigned"));

		Integer roleId = assignRole.getRoleId();

		log.info("NotificationServiceImpl:: Inside getNotifications");

		if (request.getPage() == null || request.getSize() == null) {

			return ApiResponse.failure(ResponseCode.FAILURE, "failure", List.of("page and size must be provided"));
		}

		if (request.getPage() < 0 || request.getSize() <= 0) {

			return ApiResponse.failure(ResponseCode.FAILURE, "failure", List.of("Invalid page or size values"));
		}

		Sort sort = Sort.by("DESC".equalsIgnoreCase(request.getDirection()) ? Sort.Direction.DESC : Sort.Direction.ASC,

				request.getSortBy() != null ? request.getSortBy() : "notificationSentAt");

		Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

		Specification<NotificationEngineEntity> baseSpec = request.toNotificationSpecification().and(hasRoleId(roleId));

		Page<NotificationEngineEntity> pageResult = notificationEngineRepository.findAll(baseSpec, pageable);

		Specification<NotificationEngineEntity> countSpec = request.buildNotificationCountSpec().and(hasRoleId(roleId));

		long totalCount = notificationEngineRepository.count(countSpec);

		long readCount = notificationEngineRepository.count(countSpec.and(isReadEquals(true)));

		long unreadCount = notificationEngineRepository.count(countSpec.and(isReadEquals(false)));

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

		return ApiResponse.success(ResponseCode.SUCCESS, "success", response);
	}

	private Specification<NotificationEngineEntity> hasRoleId(Integer roleId) {

		return (root, query, cb) -> cb.equal(root.get("roleId"), roleId);
	}

	private Specification<NotificationEngineEntity> isReadEquals(Boolean value) {

		return (r, q, c) -> c.equal(r.get("isRead"), value);
	}

	@Override
	public ApiResponse<?> getNotificationCounts() {

		log.info("NotificationServiceImpl:: Inside getNotificationCounts");

		String authHeader = httpServletRequest.getHeader("Authorization");

		Long userId = null;

		if (authHeader != null && authHeader.startsWith("Bearer ")) {

			String token = authHeader.substring(7);

			userId = jwtService.extractUserId(token);

		} else {

			return ApiResponse.failure(ResponseCode.FAILURE, "Unauthorized", List.of("Missing or invalid token"));
		}

		AssignRolesEntity assignRole = assignRolesRepository.findByUserId(userId.intValue())
				.orElseThrow(() -> new RuntimeException("Role not assigned"));

		Integer roleId = assignRole.getRoleId();

		Long total = notificationEngineRepository.countByRoleId(roleId);

		Long read = notificationEngineRepository.countByRoleIdAndIsRead(roleId, true);

		Long unread = notificationEngineRepository.countByRoleIdAndIsRead(roleId, false);

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

		log.info("the notification ids are:" + request.getIds());

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
