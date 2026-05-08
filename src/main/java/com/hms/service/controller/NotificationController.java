package com.hms.service.controller;


import com.hms.service.request.SpecificationFilterRequest;
import com.hms.service.service.INotificationService;
import com.hms.service.wrappers.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/hms/notifications")
@Slf4j
public class NotificationController {

 
    /**
     * Test endpoint — directly fires callNotification() without going through SR submission.
     * Use this from Postman to verify Kafka → DB save → Email → WebSocket push.
     *
     * POST /hms/notifications/test
     */
    
    //for testing notification call without going through SR submission. This can be used from Postman to verify Kafka → DB save → Email → WebSocket push.
    
//    @PostMapping("/test")
//    public ApiResponse<?> testNotification(@RequestBody NotificationEvent event) {
//        log.info("NotificationController :: /test endpoint hit for SR: {}", event.getSrId());
//
// //        if (event.getTriggeredAt() == null) {
// //           event.setTriggeredAt(LocalDateTime.now());
// //      }
//
//        // roleEmailMap is already inside the event body sent from Postman
//        //notificationService.callNotification(event.getRoleEmailMap(), event);
//        notificationService.callNotification(event);
//
//        return ApiResponse.success(ResponseCode.SUCCESS,
//                "Notification triggered successfully for SR: " + event.getSrId(), null);
//    }
    
    
    @Autowired
    private INotificationService iNotificationService;

    @PostMapping("/list")
    public ApiResponse<?> getNotifications(@RequestBody SpecificationFilterRequest request) {
        return iNotificationService.getNotifications(request);
    }

    @GetMapping("/counts")
    public ApiResponse<?> getCounts() {
        return iNotificationService.getNotificationCounts();
    }
}

