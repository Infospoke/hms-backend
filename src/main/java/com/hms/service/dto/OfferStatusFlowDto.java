package com.hms.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OfferStatusFlowDto {

    private Long offerRequestByHR;

    private Long underReviewApproval;

    private Long offerReleased;

    private Long offerAccepted;

    private Long offerRejected;

}