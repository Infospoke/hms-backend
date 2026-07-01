package com.hms.service.response;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class InterviewScheduleDetailsResponse {
	
	private LocalDate InterviewDate;
	
	private LocalTime startTime;
	
	private LocalTime endTime;
	
	private String InterviewType;
	
}
