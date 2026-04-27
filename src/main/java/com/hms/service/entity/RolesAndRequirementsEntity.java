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
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="tb_roles_requirements")
public class RolesAndRequirementsEntity {
	
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id",updatable=false,nullable=false)
	private Integer id;
	
	@Column(name = "sr_id", unique = true, nullable = false)
	private String srId;
	
	@Column(name="skills_must_have",length=1000)
	private String skillsMustHave;
	
	@Column(name="nice_to_have_skills",length=1000)
	private String niceToHaveSkills;
	
	@Column(name="educationRequirements")
	private String educationRequirement;
	
	@Column(name="travelRequirements")
	private String travelRequirement;;
	
	@Column(name="min_experience")
	private Integer minExperience;
	
	@Column(name="max_experience")
	private Integer maxExperience;
	
	@Column(name="min_interviews")
	private Integer minInterviewRounds;
	
	@Column(name="max_interviews")
	private Integer maxInterviewRounds;
	
	@Column(name="certifications_required",length=1000)
	private String certificationsRequired;
	
	@Column(name="languages")
	private String languages;
	
	@Column(name="assessment_required")
	private Boolean assessmentRequired = false;
	
	
	@Column(name="submitted")
	private Boolean submitted;
	
	@Column(name="approved")
	private Boolean approved;

}
