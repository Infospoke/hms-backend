package com.hms.service.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InterviewFeedbackRequest {
	
	private Integer jobId;
	
	private Integer applicantId;
	
	private String interviewType;
	
	private String roundType;
	
	private String decision;
	
	private Integer overallRating;
	
	private Integer technicalKnowledge;
	
	private Integer culturalFit;
	
	private Integer analyticalThinking;
	
	private Integer problemSolving;
	
	private Integer communication;
	
	private String strengths;
	
	private String areasOfImprovemnets;
	
	private String additionalComments;
	
	private Integer currentStageId;

}
