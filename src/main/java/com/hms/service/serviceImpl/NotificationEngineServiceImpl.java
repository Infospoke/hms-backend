package com.hms.service.serviceImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.hms.service.entity.NotificationEngineEntity;
import com.hms.service.repository.NotificationEngineRepository;
import com.hms.service.request.SpecificationFilterRequest;
import com.hms.service.service.INotificationService;
import com.hms.service.wrappers.ApiResponse;
import com.hms.service.wrappers.ResponseCode;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class NotificationEngineServiceImpl implements INotificationService {

    @Autowired
    private NotificationEngineRepository notificationEngineRepository;

    
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
}