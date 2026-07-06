package com.hms.service.response;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InterviewUpcomingListResponse {

	private Integer scheduleId;

	private Integer applicantId;

	private String candidateName;

	private String jobTitle;

	private String department;

	private String round;

	private String interviewMode;

	private LocalDate interviewDate;

	private LocalTime startTime;

	private LocalTime endTime;

	private String meetingLink;

	private String venueDetails;
	
	private String roundProgress;

}
