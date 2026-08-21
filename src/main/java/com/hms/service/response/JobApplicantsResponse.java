package com.hms.service.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.hms.service.dto.CompletedStageDto;
import com.hms.service.entity.CandidateCreationDetailsEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data

@AllArgsConstructor

@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class JobApplicantsResponse {

	private Integer id;
	private Integer jobId;
	private String candidateId;
	private String jobCode;
	private String firstName;
	private String lastName;
	private String email;
	private String phNo;
	private String Resume;
	private String additionalFile;
	private String coverLetterDescription;
	private Boolean privacyPolicy;
	private Boolean contactFutureOpportunities;
	private Integer createdBy;
	private LocalDateTime CreatedDate;
	private String status;
	private String screenedStatus;
	private String jobTitle;
	private long totalApplicants;
	private Map<String, Long> report = new LinkedHashMap<>();
	private String slaColor;
	private String currentStage;
	private String planName;
	private String location;
	private Integer minExperience;
	private Integer maxExperience;
	private String department;
	private LocalDate interviewDate;
	private LocalTime startTime;
	private LocalTime endTime;
	private Integer currentStageType;
	private LocalDate rescheduleDate;
	private LocalTime rescheduleStartTime;
	private LocalTime rescheduleEndTime;  
	private Integer noOfStages;

	private Integer completedStages;

	private List<CompletedStageDto> completedStageDetails;
	
}
