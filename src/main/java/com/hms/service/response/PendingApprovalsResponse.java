package com.hms.service.response;

import java.time.LocalDateTime;
import java.util.List;

import com.hms.service.dto.ApprovalStatusDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PendingApprovalsResponse {
	
private Integer offerId;
	
	private Integer applicationId;
	
	private String applicantName;
	
	private String applicantEmail;
	
	private String department;
	
	private String jobTitle;
	
    private List<ApprovalStatusDto> approvals;
    
    private LocalDateTime requestedOn;
    
    private String priority;
     

}
