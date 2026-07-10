package com.hms.service.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRaiseOfferRequest {

	    private Integer applicantId;

	    private Long totalCtc;

	    private String noticePeriod;

	    private String probationPeriod;

	    private String offerLetterTemplate;

	    private String compensation;

	    private Boolean submitFinancialApproval;

}
