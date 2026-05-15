package com.hms.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebSocketNotification {

    private String processId;
    private String title;
    private String message;
    private String deptName;
    private String type;
    private Integer roleId;
}
