package com.hms.service.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicantsCountResponse {
	
	private Long applicantCount;
	
	private Long resumeCount;
	
	private Long shortlisted;
	
	private Long interviewCount;
	
	private Long offerAccepted;
	
	private Long hiredCount;

}
