package com.hms.service.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BudgetAndCompensationResponse {

    private Integer id;
    private String srId;

    private Integer proposedTotalCompensation;
    private Boolean signingBonus;
    private Boolean equity;
    private Boolean relocationBudget;

    private Integer signingBonusAmount;
    private Integer equityAmount;
    private Integer relocationBudgetAmount;
    private Long annualHiringCost;

    private Boolean draft;
    private Boolean submitted;
    private Boolean approved;
}
