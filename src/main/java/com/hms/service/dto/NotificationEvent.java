package com.hms.service.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
@JsonIgnoreProperties(ignoreUnknown = true) 
public class NotificationEvent implements Serializable {

    private String processId;
    //private String jobTitle;
    private String makerRoleName;
    private String checkerRoleName;
    private String deptName;
    private String makerNotificationTitle;
    private String checkerNotificationTitle;
    private String makerMessage;

    private String checkerMessage;

    private Integer makerRoleId;
    private Map<Integer, List<String>> roleEmailMap;  // roleId -> [email1, email2, ...]
    private String makerEmailAddress;
    private String checkerEmailBody;
  
    private String makerEmailBody;
    private String type;
    private LocalDateTime triggeredAt;
    private Integer makerId;
    private Integer checkerId;
    private String checkerRoleIds;
}
