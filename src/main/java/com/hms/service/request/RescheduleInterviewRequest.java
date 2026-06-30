package com.hms.service.request;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RescheduleInterviewRequest {

	private Integer scheduleId;
	private LocalDate rescheduleDate;
	private LocalTime rescheduleStartTime;
	private LocalTime rescheduleEndTime;
	private String rescheduleMeetingLink;
	private String rescheduleVenueDetails;
}
