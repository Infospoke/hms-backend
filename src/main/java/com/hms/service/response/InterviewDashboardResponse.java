package com.hms.service.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InterviewDashboardResponse {
	
	private Integer allClearedCandidates;
	private Integer aiInterview;
	private Integer technicalRound;
	private Integer managerialRound;
	private Integer hrRound;

}
