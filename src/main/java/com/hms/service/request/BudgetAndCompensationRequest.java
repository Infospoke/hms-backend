package com.hms.service.request;

import org.springframework.boot.convert.DataSizeUnit;

import com.hms.service.constants.Constants;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BudgetAndCompensationRequest {

	private Integer proposedTotalCompensation;
	
	private Boolean signingBonus;
	
	private Boolean equity;
	
	private Boolean relocationBudget;
	
	private Integer signingBonusAmount;
	
	private Integer equityAmount;
	
	private Integer relocationBudgetAmount;
	
	private Integer AnnualHiringCost;

}
