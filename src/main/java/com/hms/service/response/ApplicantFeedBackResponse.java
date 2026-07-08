package com.hms.service.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class ApplicantFeedBackResponse {

	private Integer applicantId;

	private String interviewType;

	private Integer currentStageId;

	private Integer overallRating;

	private Integer technicalKnowledge;

	private Integer communication;

	private Integer problemSolving;

	private Integer analyticalThinking;

	private Integer culturalFit;

	private String strengths;

	private String areasOfImprovemnets;

	private String additionalComments;

	private String decision;

	private String interviewMode;

}
