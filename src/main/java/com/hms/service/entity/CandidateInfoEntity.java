package com.hms.service.entity;

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
@Table(name = "tb_candidate_info")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CandidateInfoEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id" , nullable = false)
	private Integer id;

	@Column(name = "first_name")
	private String firstName;

	@Column(name = "last_name")
	private String lastName;

	@Column(name = "phone_number")
	private String phoneNumber;

	@Column(name = "email")
	private String email;

	@Column(name = "job_country")
	private String jobCountry;
	
	@Column(name="job_title")
	private String JobTitle;
	
	@Column(name="jobId")
	private Integer jobId;

	@Column(name = "created_date")
	private LocalDateTime createdDate;

	@Column(name = "updated_date")
	private LocalDateTime updatedDate;

	@Column(name = "department")
	private String department;

	@Column(name = "status")
	private String status;

	@Column(name = "description")
	private String description;
	
	@Column(name = "github_Url")
	private String githubURL;
	
	@Column(name = "linkedin_Url")
	private String linkedinURL;
	
	@Column(name = "accepted_date")
	private LocalDateTime acceptedDate;
	
	@Column(name="application_id")
	private Integer applicationId;
	

}
