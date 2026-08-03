package com.hms.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NegotiationFlowDto {

    private Long negotiationRequest;

    private Long hrReview;

    private Long underReview;

    private Long reReleaseOffer;

    private Long candidateAccepted;

    private Long candidateRejected;

}