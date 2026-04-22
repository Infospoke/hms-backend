package com.hms.service.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BudgetAndCompensationRequest {
	
	private String id;
	
	private String srId;
	
    private Integer minSalary;
	
	private Integer maxSalary;

	private Integer proposedTotalCompensation;
	
	private Boolean signingBonus;
	
	private Boolean equity;
	
	private Boolean relocationBudget;
	
	private Integer signingBonusAmount;
	
	private Integer equityAmount;
	
	private Integer relocationBudgetAmount;
	
	private Integer AnnualHiringCost;

}
