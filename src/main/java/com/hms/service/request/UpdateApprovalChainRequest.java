package com.hms.service.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateApprovalChainRequest {
	private Integer id;
	private String status;
	private String approval;
	private String approvedComments;
	private String rejectedComments;
	

}
