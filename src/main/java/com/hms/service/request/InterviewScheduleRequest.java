package com.hms.service.request;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InterviewScheduleRequest {
	
	private Integer applicantId;
	
	private String roundType;

	private LocalDate interviewDate;

	private LocalTime startTime;

	private LocalTime endTime;

	private String interviewType;

	private String meetingLink;

	private String venueDetails;

}
