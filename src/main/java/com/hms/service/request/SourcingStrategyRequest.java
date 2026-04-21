package com.hms.service.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SourcingStrategyRequest {
	
	private Boolean internalBoard = false;
	 
	private Boolean naukri = false;
 
	private Boolean linkedIn = false;
 
	private Boolean indeed = false;
 
	private Boolean companySite = false;
 
	private Boolean agencyRpo = false;
 
	private Boolean internalFirstPolicy = true;
 
	private Integer sourcingBudget;
 
	private Boolean referralEnabled = false;
 
	private Double referralAmount;
 
	private Boolean diversityEnabled = false;
 
	private String diversityTags;

}
