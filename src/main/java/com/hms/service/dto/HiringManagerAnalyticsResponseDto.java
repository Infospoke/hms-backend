package com.hms.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HiringManagerAnalyticsResponseDto {

	private CandidatePipelineDto candidatePipeline;

	private OfferStatusFlowDto offerStatusFlow;

	private NegotiationFlowDto negotiationFlow;

	private CandidateQualityDto candidateQuality;

	private HiringHealthDto hiringHealth;

}