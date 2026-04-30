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
	private LocalDate createdOn;
	
	@Column(name="created_by")
	private String createdBy;


}
