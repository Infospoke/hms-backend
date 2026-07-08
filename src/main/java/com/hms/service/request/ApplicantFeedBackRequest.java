package com.hms.service.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicantFeedBackRequest {
	
	private Integer applicantId;
	
	private Integer currentStageId;

}
