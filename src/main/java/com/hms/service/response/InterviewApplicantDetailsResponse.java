package com.hms.service.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class InterviewApplicantDetailsResponse {
	
	private String applicantName;
	
	private String applicantEmail;
	
	private String applicantPhoneNumber;
	
	private String jobTitle;
	
	private String jobCode;
	
	private String department;
	
	private Integer minExperience;
	
	private Integer maxExperience;
	
	private String scheduledBy;
	
	private LocalDateTime interviewMailSentAt;
	
	private LocalDateTime interviewScheduledAt;
	
	private List<Object> questions;
	
	private List<String> questionType;
	
	private String questionDifficulty;
	
	private Integer noOfQuestions;

	private String candidateId;
  	
	

	
}
