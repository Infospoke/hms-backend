package com.hms.service.request;

import jakarta.validation.constraints.Size;
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
	@Size(min = 3, max = 300, message = "Approved comments must be between 3 and 300 characters")
	private String approvedComments;
	@Size(min = 3, max = 300, message = "Rejected comments must be between 3 and 300 characters")
	private String rejectedComments;
	@Size(min = 3, max = 300, message = "Deactivate comments must be between 3 and 300 characters")
	private String deactivateComments;
	@Size(min = 3, max = 300, message = "Activate comments must be between 3 and 300 characters")
	private String activateComments;

	private Boolean activeApproval;

	private Boolean deactiveApproval;

}
