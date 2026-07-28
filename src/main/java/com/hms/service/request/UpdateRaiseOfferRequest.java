package com.hms.service.request;

import java.time.LocalDate;

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

	    private Integer offerLetterTemplateId;

	    private String compensation;

	    private Boolean submitFinancialApproval;
	    
	    private LocalDate joiningDate;

}
