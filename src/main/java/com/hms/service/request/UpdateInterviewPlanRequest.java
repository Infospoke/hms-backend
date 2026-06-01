package com.hms.service.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateInterviewPlanRequest {

	private Integer id;

	private String approval;

	private String comments;

	private String status;

	private Boolean activeApproval;

	private Boolean deactiveApproval;

	private String description;
	
	
}
