package com.hms.service.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentTimelineResponse {

    private String action;

    private String comments;

    private String description;
    
    private String createdBy;
    
    private LocalDateTime createdAt;
}