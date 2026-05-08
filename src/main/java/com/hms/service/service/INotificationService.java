package com.hms.service.service;



import com.hms.service.dto.NotificationEvent;
import com.hms.service.request.SpecificationFilterRequest;
import com.hms.service.wrappers.ApiResponse;

public interface INotificationService {

    ApiResponse<?> getNotificationCounts();

	ApiResponse<?> getNotifications(SpecificationFilterRequest request);
	
    void callNotification(NotificationEvent event);

}