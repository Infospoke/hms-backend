package com.hms.service.request;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InterviewCompleteRequest {
	
	private Integer applicantId;
	
	private Integer currentStageType;
	
	private LocalDateTime interviewCompltedOn;
	
	private Boolean interviewCompelted;

}
