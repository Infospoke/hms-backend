package com.hms.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CandidatePipelineDto {

	private Long applied;

	private Long screening;

	private Long interview;

	private Long offer;

	private Long hired;

	private Double screeningPercentage;

	private Double interviewPercentage;

	private Double offerPercentage;

	private Double hiredPercentage;

	private Double overallConversionRate;

}