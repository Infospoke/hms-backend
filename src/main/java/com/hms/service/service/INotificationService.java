package com.hms.service.service;

import com.hms.service.dto.NotificationEvent;
import java.util.List;
import java.util.Map;

public interface INotificationService {
    
    void callNotification(NotificationEvent event);
}
