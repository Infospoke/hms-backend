package com.hms.service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_job_applications")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobApplicationEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false, updatable = false)
	private Integer id;

	@Column(name = "job_id")
	private Integer jobId;

	@Column(name = "first_name")
	private String firstName;

	@Column(name = "last_name")
	private String lastName;

	private String email;

	@Column(name = "ph_no")
	private String phNo;

	@Column(name = "resume")
	private String resume;

	@Column(name = "additional_file")
	private String additionalFile;

	@Column(name = "cover_letter_description")
	private String coverLetterDescription;

	@Column(name = "privacy_policy")
	private Boolean privacyPolicy;

	@Column(name = "contact_future_opportunities")
	private Boolean contactFutureOpportunities;

	@Column(name = "created_by")
	private Integer createdBy;
	
	@Column(name="referral")
	private Boolean referral;

	@Column(name = "created_date")
	private LocalDateTime createdDate;
	
	@Column(name = "source")
	private String source;
	
	@Column(name = "stage_entry_date")
	private LocalDateTime stageEntryDate;

	@Column(name = "sla_days")
	private Integer slaDays;

	@Transient
	private Long daysInStage;

	@Transient
	private String slaColor;
	
	@Column(name = "current_stage")
	private String currentStage;

	@Column(name = "job_status")
	private String jobStatus;
	
	@Column(name = "rejected")
	private Boolean rejected;

}
