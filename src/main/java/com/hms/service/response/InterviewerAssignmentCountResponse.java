package com.hms.service.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InterviewerAssignmentCountResponse {

	private Integer totalAssignments;
	private Integer acceptedCount;
	private Integer rejectedCount;
	private Integer pendingCount;

}
