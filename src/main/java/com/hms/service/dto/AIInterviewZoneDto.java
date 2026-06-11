package com.hms.service.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AIInterviewZoneDto {

    private Integer applicationId;
    private String applicantName;
    private String jobTitle;
    private Integer numberOfQuestions;
    private Boolean questionStatus;
    private LocalDateTime updatedDate;
    private String email;
}