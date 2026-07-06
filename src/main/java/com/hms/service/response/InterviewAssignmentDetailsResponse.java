package com.hms.service.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InterviewAssignmentDetailsResponse {

		private String stageName;

		private String jobTitle;

		private String deptName;

		private String interviewType;

		private String interviewMode;

		private LocalDateTime assignedOn;

		private String responseDue;

		private String assignedBy;
	

}
