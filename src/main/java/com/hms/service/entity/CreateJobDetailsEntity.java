package com.hms.service.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
@Table(name="tb_create_job_details")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateJobDetailsEntity {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Integer id;
    
	@Column(name="job_title")
	private String jobTitle;
	
	@Column(name="business_unit")
	private Integer businessUnitId;
	
	@Column(name="department")
	private Integer departmentId;
	
	@Column(name="location")
	private String location;
	
	@Column(name="job_code",unique=true)
	private String jobCode;
	
	@Column(name="openings")
	private Integer openings;
	
	@Column(name="target_start_date")
	private LocalDate targetStartDate;
	
	@Column(name="work_mode")
	private String workMode;
	
	@Column(name="employment_type")
	private String employmentType;
	
	@Column(name="skills_must_have",length=1000)
	private String skillsMustHave;
	
	@Column(name="nice_to_have_skills",length=1000)
	private String niceToHaveSkills;
	
	@Column(name="min_experience")
	private Integer minExperience;
	
	@Column(name="max_experience")
	private Integer maxExperience;
	
	@Column(name="additional_notes",length=1000)
	private String additionalNotes;
	
	@Column(name="submit")
	private Boolean submit=false;
	
	@Column(name="created_by")
	private String createdBy;
	
	@Column(name="created_at")
	private LocalDateTime createdAt;

	@Column(name="updated_by")
	private String updatedBy;
	
	@Column(name="updated_at")
	private LocalDateTime updatedAt;

	
	

}
