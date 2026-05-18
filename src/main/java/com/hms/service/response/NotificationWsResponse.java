package com.hms.service.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true) 

public class NotificationWsResponse {
	private Long id;
    private String notificationTitle;
    private String message;
    private String processId;
    private Integer roleId;
    private String deptName;
    private String roleName;
    private Integer deptId;
    private LocalDateTime notificationSentAt;
    private Boolean isRead;

}
