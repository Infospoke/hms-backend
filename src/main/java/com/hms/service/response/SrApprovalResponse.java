package com.hms.service.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SrApprovalResponse {
	private String srId;
	private String jobTitle;
	private String Department;
	private String overAllStatus;
	private LocalDateTime submittedOn;
	private String CurrentStage;
	
	

}
