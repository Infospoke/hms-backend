package com.hms.service.response;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SrApprovalResponse {
	private String srId;
	private String jobTitle;
	private Integer Department;
	private String overAllStatus;
	private LocalDate createdOn;
	private String CurrentStage;
	
	

}
