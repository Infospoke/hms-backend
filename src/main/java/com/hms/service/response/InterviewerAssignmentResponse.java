package com.hms.service.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InterviewerAssignmentResponse {

    private Long id;

    private Integer jobId;

    private Integer planId;

    private Long roundId;

    private String stageName;

    private Long interviewerUserId;

    private String interviewerName;

    private String roleName;

    private String status;

    private String comments;

    private LocalDateTime respondedAt;
}