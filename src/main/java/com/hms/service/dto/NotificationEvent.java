package com.hms.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Kafka message payload for notifications.
 * Published by NotificationServiceImpl and consumed by the same service's @KafkaListener.
 *
 * roleEmailMap — key: roleId, value: list of emails for that role.
 * Notifications and emails are sent to every email in the map.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationEvent implements Serializable {

    private String srId;
    private String jobTitle;
    private String deptName;
    //private String makerNotificationTitle;
    private String checkerNotificationTitle;
    private String message;
    private Map<Integer, List<String>> roleEmailMap;  // roleId -> [email1, email2, ...]
    private String makerEmail;
    private String emailBody;
    //private LocalDateTime triggeredAt;
}
