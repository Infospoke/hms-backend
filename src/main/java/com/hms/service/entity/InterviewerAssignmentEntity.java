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
@Table(name = "tb_interview_assignment")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InterviewerAssignmentEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "job_id")
	private Integer jobId;

	@Column(name = "plan_id")
	private Integer planId;

	@Column(name = "round_id")
	private Long roundId;

	@Column(name = "stage_name")
	private String stageName;

	@Column(name = "interviewer_user_id")
	private Long interviewerUserId;

	@Column(name = "interviewer_name")
	private String interviewerName;

	@Column(name = "role_name")
	private String roleName;

	@Column(name = "status")
	private String status;

	@Column(name = "comments", columnDefinition = "TEXT")
	private String comments;

	@Column(name = "responded_at")
	private LocalDateTime respondedAt;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "created_at")
	private LocalDateTime createdAt;
	
	@Column(name = "job_title")
	private String jobTitle;
	
	@Column(name = "dept_name")
	private String deptName;
	
	@Column(name = "plan_name")
	private String planName;
	
	@Column(name="user_id")
	private Long userId;

	
}