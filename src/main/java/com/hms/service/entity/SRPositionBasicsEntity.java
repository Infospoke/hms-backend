package com.hms.service.entity;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.JoinColumn;
@Entity
@Table(name="tb_staffing_requisition")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SRPositionBasicsEntity {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id",updatable=false,nullable=false)
	private Integer id;
	
	@Column(name="job_title")
	private String jobTitle;
	
	@Column(name = "sr_id", unique = true, nullable = false)
	private String srId;
	
	@Column(name="business_unit")
	private Integer businessUnitId;
	
	@Column(name="department")
	private Integer departmentId;
	
	@ElementCollection
    @CollectionTable(name = "tb_child_reporting_manager_info", joinColumns = @JoinColumn(name = "staffing_requisition_id"))
    @Column(name = "reporting_manager_ids")
    private List<Integer> reportingManagerInfo;						
	
	@Column(name="location")
	private String location;
	
	@Column(name="seniority_level")
	private String seniorityLevel;
	
	@Column(name="openings")
	private Integer openings;
	
	@Column(name="target_start_date")
	private LocalDate targetStartDate;
	
	@Column(name="workMode")
	private String workMode;
	
	@Column(name="employmentType")
	private String employmentType;
	
	@Column(name="priority")
	private String priority;
	
	@Column(name="requisition_type")
	private String requisitionType;
	
	@Column(name="business_case",length = 2000)
	private String businessCase;
	
	@Column(name="impact_if_not_filled",length=2000)
	private String impactIfNotFilled;
	
	@Column(name="replaces_employee")
	private Integer replacesEmployee;
	
	@Column(name="document")
	private String document;
	
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
	
	
	
	
	
   
	
	

}
