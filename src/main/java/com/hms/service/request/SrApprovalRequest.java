package com.hms.service.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SrApprovalRequest {
	
	private String srId;
	
	private Boolean approved;
	
	private Boolean rejected;
	
	private String approvedBy;
	
	private String approver1By;
	
	private String approver2By;
	
	private String approver3By;
	
	private String approver1Comments;
	
	private String approver2Comments;

	private String approver3Comments;

	private String rejectedBy;
	
	
	

}
