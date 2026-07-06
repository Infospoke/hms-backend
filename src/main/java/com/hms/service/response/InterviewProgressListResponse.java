package com.hms.service.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InterviewProgressListResponse {
	private Integer applicationId;

	private String candidateName;

	private String email;

	private String jobTitle;

	private String department;

	private Integer completedRounds;

	private Integer totalRounds;

	private Integer currentStageId;

	private String currentStage;

	private LocalDateTime lastActivity;

	private List<InterviewRoundResponse> roundDetails;

}
