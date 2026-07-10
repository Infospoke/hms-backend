package com.hms.service.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApproveOfferRequest {
	
	private Integer applicantId;
	
	private String approve;
	
	private String reject;
	
	private String comments;
	
	

}
