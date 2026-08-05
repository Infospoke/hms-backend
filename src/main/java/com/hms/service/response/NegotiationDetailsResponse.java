package com.hms.service.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.hms.service.request.HrRecommendation;
import com.hms.service.request.Negotiation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NegotiationDetailsResponse {

    private Integer negotiationId;

    private Integer applicantId;

    private String candidateId;

    private String candidateName;

    private String email;

    private Integer jobId;

    private String jobTitle;

    private String srId;

    private Long minimumSalary;

    private Long maximumSalary;

    private Long annualHiringCost;

    private Long totalRequestedAmount;

    private LocalDate joiningDate;

    private String joiningDateReason;

    private String overallJustification;

    private String others;
    
    private LocalDateTime offerReleasedOn;

    private List<String> supportingDocuments;

    private List<Negotiation> negotiation;
    
    private List<HrRecommendation> hrRecommendations;

    private Long hrRecommendedCtc;

    private String hrReason;

    private LocalDate revisedJoiningDate;
    
    private List<NegotiationReviewResponse> approvalStages;

}
