package com.hms.service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@Table(name="tb_budget_compensation")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BudgetAndCompensationEntity {
	
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id",updatable=false,nullable=false)
	private Integer id;
	
	@Column(name="sr_id")
	private String srId;
	
	@Column(name="proposed_total_compensation")
	private Integer proposedTotalCompensation;
	
	@Column(name="signing_bonus")
	private Boolean signingBonus;
	
	@Column(name="equity")
	private Boolean equity;
	
	@Column(name="relocationBudget")
	private Boolean relocationBudget;
	
	@Column(name="signing_bonus_amount")
	private Integer signingBonusAmount;
	
	@Column(name="equityAmount")
	private Integer equityAmount;
	
	@Column(name="relocation_budget_amount")
	private Integer relocationBudgetAmount;
	
	@Column(name="annual_hiring_cost")
	private Integer AnnualHiringCost;
	
	@Column(name="budget_compensation_status")
	private String budgetCompensationStatus;
	
	@Column(name="status")
	private String status;

	
	@Column(name="submitted")
	private Boolean submitted;
	
	@Column(name="approved")
	private Boolean approved;

}
