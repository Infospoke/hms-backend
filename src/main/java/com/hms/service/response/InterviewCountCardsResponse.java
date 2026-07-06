package com.hms.service.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InterviewCountCardsResponse {

	    private Long assignedInterviewRequests;

	    private Long toSchedule;

	    private Long upcomingInterviews;

	    private Long feedbackPending;
	

}
