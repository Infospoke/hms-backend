package com.hms.service.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
@Table(name="tb_sr_position_basics")
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
	
	@Column(name="business_unit")
	private Integer businessUnitId;
	
	@Column(name="department")
	private Integer departmentId;
	
	@ElementCollection
	@CollectionTable(
	    name = "tb_child_reporting_manager_info",
	    joinColumns = @JoinColumn(name = "staffing_requisition_id", referencedColumnName = "id")
	)
	@Column(name = "reporting_manager_ids")
	private List<Integer> reportingManagerInfo;				
	
	@Column(name="location")
	private String location;
	
	@Column(name="country")
	private String country;
	
	@Column(name="seniority_level")
	private Integer seniorityLevel;
	
	@Column(name="openings")
	private Integer openings;
	
	@Column(name="target_start_date")
	private LocalDate targetStartDate;
	
	@Column(name="work_mode")
	private String workMode;
	
	@Column(name="employment_type")
	private String employmentType;
	
	@Column(name="priority")
	private String priority;
	
	@Column(name="submitted")
	private Boolean submitted;
	
	@Column(name="approved")
	private Boolean approved;
	
	@Column(name = "sr_id", unique = true)
	private String srId;
	
	@Column(name="created_on")
	private LocalDateTime createdOn;
	
	@Column(name="submitted_on")
	private LocalDateTime submittedOn;
	
	@Column(name="created_by")
	private String createdBy;
	
	@Column(name = "user_id")
	private Long userId;
	
	@Column(name="role_name")
	private String roleName;
	
	@Column(name="in_progress")
	private Boolean inProgress=false;
	
	@Column(name="approver1")
	private Boolean approver1=false;
	
	@Column(name="approver2")
	private Boolean approver2=false;
	
	@Column(name="approver3")
	private Boolean approver3=false; 

	@Column(name="approver1_by")
	private String approver1By;
	
	@Column(name="approver2_by")
	private String approver2By;
	
	@Column(name="approver3_by")
	private String approver3By;
	
	@Column(name="date_of_approval1")
	private LocalDateTime dateOfApproval1;
	
	@Column(name="date_of_approval2")
	private LocalDateTime dateOfApproval2;
	
	@Column(name="date_of_approval3")
	private LocalDateTime dateOfApproval3;
	
	@Column(name="rejected")
	private Boolean rejected=false;
	
	@Column(name="rejected_by")
	private String rejectedBy;
	
	@Column(name="functionality_id")
	private Integer functionalityId;
	
	@Column(name="comments_by_approver1")
	private String commentsByApprover1;
	
	@Column(name="comments_by_approver2")
	private String commentsByApprover2;
	
	@Column(name="comments_by_approver3")
	private String commentsByApprover3;
	
	@Column(name="approver1_role")
	private String approver1Role;
	
	@Column(name="approver2_role")
	private String approver2Role;
	
	@Column(name="approver3_role")
	private String approver3Role;
	
	@Column(name="maker_role_id")
	private Integer makerRoleId;
	

	@Column(name="current_stage")
	private String currentStage;
	
	@Column(name="job_submit")
	private Boolean jobSubmit=false;

}
