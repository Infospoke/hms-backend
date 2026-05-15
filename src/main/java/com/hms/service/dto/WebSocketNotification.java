package com.hms.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true) 

public class WebSocketNotification {

    private String processId;
    private String title;
    private String message;
    private String deptName;
    private String type;
    private Integer roleId;
}
