package com.hms.service.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRecruitersAssignmentRequest {

	private Integer jobId;
	
	private String status;
	
	private String comments;
}
