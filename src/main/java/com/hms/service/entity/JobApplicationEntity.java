package com.hms.service.entity;

import java.time.LocalDateTime;

import com.hms.service.enums.ReuploadStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

	@Column(name = "referral")
	private Boolean referral;

	@Column(name = "created_date")
	private LocalDateTime createdDate;

	@Column(name = "source")
	private String source;

	@Column(name = "stage_entry_date")
	private LocalDateTime stageEntryDate;

	@Column(name = "current_stage")
	private String currentStage;

	@Column(name = "job_status")
	private String jobStatus;

	@Column(name = "recruiter_id")
	private Integer recruiterId;

	@Column(name = "rejected")
	private Boolean rejected;

	@Column(name = "in_person_interviews")
	private boolean inPersonInterviews;

	@ManyToOne
	@JoinColumn(name = "candidate_id", referencedColumnName = "candidate_id")
	private CandidateCreationDetailsEntity candidate;

	@Enumerated(EnumType.STRING)
	@Column(name = "reupload_status")
	private ReuploadStatus reuploadStatus;

	@Column(name = "career_portal")
	private Boolean careerPortal = false;
}
