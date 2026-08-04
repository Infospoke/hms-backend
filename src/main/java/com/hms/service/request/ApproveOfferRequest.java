package com.hms.service.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApproveOfferRequest {
	
	private Integer applicantId;
	
	private Boolean approve;
	
	private Boolean reject;
	
	private String comments;
	
	private String eSignature;
	
    private String approvalType;          // NORMAL_APPROVAL / NEGOTIATION

    private List<FinanceRecommendation> financeRecommendations;

    private String financeReason;
	

}
