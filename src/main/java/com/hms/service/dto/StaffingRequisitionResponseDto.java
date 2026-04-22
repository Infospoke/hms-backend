package com.hms.service.dto;

import com.hms.service.response.BudgetAndCompensationResponse;
import com.hms.service.response.BusinessJustificationResponse;
import com.hms.service.response.PositonBasicsResponse;
import com.hms.service.response.RolesAndRequirementsResponse;
import com.hms.service.response.SourcingStrategyResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StaffingRequisitionResponseDto {

    private PositonBasicsResponse positonBasicsResponse;
    private BusinessJustificationResponse businessJustificationResponse;
    private BudgetAndCompensationResponse budgetAndCompensationResponse;
    private RolesAndRequirementsResponse rolesAndRequirementsResponse;
    private SourcingStrategyResponse sourcingStrategyResponse;
}
