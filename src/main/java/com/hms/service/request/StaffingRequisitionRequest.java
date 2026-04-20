package com.hms.service.request;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StaffingRequisitionRequest {

	private String jobTitle;
  
	private Integer departmentId;
	
	private Integer businessUnitId;
	
	private List<Integer> reportingManagerInfo;
	
	private String location;
	
	private String seniorityLevel;
	
	private Integer openings;
	
	private LocalDate targetStartDate;

	private String workMode;
	
	private String EmploymentType;
	
	private String priority;
	
	private String requisitionType;
	
	private String businessCase;
	
	private String impactIfNotFilled;
	
	private Integer replacesEmployee;
	
	private String document;
	
	
	private Integer proposedTotalCompensation;
	
	private Boolean signingBonus;
	
	private Boolean equity;
	
	private Boolean relocationBudget;
	
	private Integer signingBonusAmount;
	
	private Integer equityAmount;
	
	private Integer relocationBudgetAmount;
	
	private Integer AnnualHiringCost;
	

	

	

}
