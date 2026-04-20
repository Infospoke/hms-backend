package com.hms.service.entity;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.JoinColumn;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "tb_staffing_requistionsEntities")

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StaffingRequisitionEntitys {
	
	    @Id
	    private String id;
	 
	    private String jobTitle;
	 
		private Integer businessUnitId;
		
		private Integer departmentId;
		
		@ElementCollection
	    @CollectionTable(name = "tb_child_reporting_manager_info", joinColumns = @JoinColumn(name = "staffing_requisition_id"))
	    @Column(name = "reporting_manager_ids")
	    private List<Integer> reportingManagerInfo;						
		
		private String location;
		
		private String seniorityLevel;
		
		private Integer openings;
		
		private LocalDate targetStartDate;
		
		private String workMode;
		
		private String employmentType;
		
		private String priority;
		
		private String requisitionType;
		
		private String businessCase;
		
		private String impactIfNotFilled;
		
		private Integer replacesEmployee;
		
		private String document;
		
		private Integer proposedTotalCompensation;
		
		private Boolean signingBonus = false;
		
		private Boolean equity = false;
		
		private Boolean relocationBudget = false;
		
		private Integer signingBonusAmount;
		
		private Integer equityAmount;
		
		private Integer relocationBudgetAmount;
		
		private Integer AnnualHiringCost;
		
		
		
		
	
	

}
