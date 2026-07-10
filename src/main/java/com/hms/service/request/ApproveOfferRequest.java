package com.hms.service.request;

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
	
	

}
