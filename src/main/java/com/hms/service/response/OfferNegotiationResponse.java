package com.hms.service.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OfferNegotiationResponse {

    private Integer negotiationId;

    private String candidateId;

    private String candidateName;

    private String email;

    private String jobTitle;

    private Long requestedAmount;

    private Long offeredAmount;

    private Long approvedAmount;
    
    private LocalDate offerNegotiationDate;
    
    private String status;
    
    private LocalDateTime offerReleasedDate;
    
    private String priority;


}
