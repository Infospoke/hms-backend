package com.hms.service.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecuriterPerformanceResponseDto {

	private List<CandidateSourcePerformanceDto> candidateSourcePerformance;

	private ConversionFunnelDto recruitmentFunnel;

	private List<HiringTrendDto> hiringTrend;

}