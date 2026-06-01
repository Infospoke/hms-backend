package com.hms.service.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InterviewPlanResponse {

	private String planName;

	private String description;

	private String status;

	private String createdBy;

	private LocalDateTime createdOn;
	
	private List<InterviewRoundsResponse> interviewRoundsResponse;
	
    private List<CommentTimelineResponse> commentTimeline;
	
	

}
