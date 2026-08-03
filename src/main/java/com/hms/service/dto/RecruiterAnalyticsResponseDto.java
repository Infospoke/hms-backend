package com.hms.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecruiterAnalyticsResponseDto {

    private ConversionFunnelDto conversionFunnel;

    private OfferStatusFlowDto offerStatusFlow;

    private NegotiationFlowDto negotiationFlow;

    private SourcePerformanceDto sourcePerformance;

}