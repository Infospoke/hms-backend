package com.hms.service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hms.service.request.SpecificationFilterRequest;
import com.hms.service.service.INotificationService;
import com.hms.service.wrappers.ApiResponse;

@RestController
@RequestMapping("/hms/notifications")
public class NotificationEngineController {

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