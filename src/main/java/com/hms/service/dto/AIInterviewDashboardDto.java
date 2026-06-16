package com.hms.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AIInterviewDashboardDto {
	private Long generateAIQuestionsCount;
	private Long scheduleAIInterviewCount;
	private Long upcomingAIInterviewCount;
}