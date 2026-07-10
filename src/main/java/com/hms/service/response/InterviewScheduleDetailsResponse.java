package com.hms.service.response;

import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class InterviewScheduleDetailsResponse {
	
	private LocalDate InterviewDate;
	
	private LocalTime startTime;
	
	private LocalTime endTime;
	
	private String InterviewType;

	private LocalDate rescheduleDate;

	private LocalTime rescheduleStartTime;
	
	private LocalTime rescheduleEndTime;
	
	private String ReScheduleInterviewType;
	
}
