package com.hms.service.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateSrRequest {
	
	private String srId;
	
	private Boolean approved;
	
	private Boolean rejected;
	
	private String comments;
	
	private Boolean finalApprovalStatus;
	
	
}
