package com.hms.service.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InterviewRoundsResponse {
	
	private Integer roundOrder;

	private String stageName;

	private String stageType;

	private String interviewMode;

	private Boolean mandatory;

}
