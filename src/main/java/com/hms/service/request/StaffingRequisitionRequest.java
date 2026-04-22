package com.hms.service.request;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StaffingRequisitionRequest {
	
	@Valid
	private PositonBascicsRequest positonBascicsRequest;
	
	@Valid
	private BusinessJustificationRequest businessJustificationRequest;
	
	@Valid
	private BudgetAndCompensationRequest budgetAndCompensationRequest;
	
	@Valid
	private RolesAndRequirementsRequest rolesAndRequirementsRequest;
	
	@Valid
	private SourcingStrategyRequest sourcingStrategyRequest;

	@Valid
	private ReviewRequest reviewRequest;
	

}
