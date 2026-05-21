package com.hms.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecruiterCardsCountDto {

	private Long totalJobs;

	private Long totalAssignees;

	private Long acceptedCount;

	private Long declinedCount;

	private Long pendingCount;
}
