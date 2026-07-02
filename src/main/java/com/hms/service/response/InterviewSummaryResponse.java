package com.hms.service.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InterviewSummaryResponse {
	
  
    private String jobTitle;
    private String department;
    private String round;
    private String interviewType;
    private String employmentType;
    private String location;
    private String workMode;
    private String interviewMode;
    private String experienceRequired;
    private Integer salary;
    private String candidateName;
    private String email;
    private String phone;
    private String currentOrganization;
    private String currentLocation;
    private String totalExperience;
    private String noticePeriod;
    private String currentStage;
    private Integer roundId;
    private LocalDateTime interviewCompletedOn;

}
