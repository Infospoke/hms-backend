package com.hms.service.entity;

import java.time.LocalDateTime;
import java.time.LocalTime;

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
@Table(name="tb_interview_candidate_details_entity")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InterviewCandidateDetailsEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false, updatable = false)
	private Integer id;
	
	@Column(name="candidate_name")
	private String canidateName;
	
	@Column(name="job_title")
	private String jobTitle;
	
	@Column(name="round")
	private String round;
	
	@Column(name="interview_type")
	private String interviewType;
	
	@Column(name="time")
	private LocalTime time;
	
	@Column(name="user_id")
	private Integer userId;
	
	@Column(name="created_on")
	private LocalDateTime createdOn;
	
	@Column(name="created_by")
	private String createdBy;

}
