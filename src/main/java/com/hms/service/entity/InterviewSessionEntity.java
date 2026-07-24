package com.hms.service.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_interview_sessions")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InterviewSessionEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "application_id")
	private Integer applicationId;

	@Column(name = "job_id")
	private Integer jobId;

	@Column(name = "interview_session_id", nullable = false, unique = true, length = 255)
	private String interviewSessionId;

	@Column(name = "question_type", length = 20)
	private String questionType = "AI";

	@Column(name = "created_date")
	private LocalDateTime createdDate;

	@Column(name = "scheduled_time")
	private LocalDateTime scheduledTime;

	@Column(name = "is_scheduled")
	private Boolean isScheduled = false;
	
	@Column(name = "scheduled_by", length = 50)
	private String scheduledBy;

	@Column(name = "schedule_email_sent")
	private Boolean scheduleEmailSent = false;

	@Column(name = "is_deleted")
	private Boolean isDeleted = false;

	@Column(name = "exam_exit_password", length = 255)
	private String examExitPassword;

	@Column(name = "status")
	private String status;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "application_id", referencedColumnName = "application_id", insertable = false, updatable = false)
	private ResumeAnalysisEntity applicant;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "job_id", referencedColumnName = "job_id", insertable = false, updatable = false)
	private CreateJobDetailsEntity job;
	
	@Column(name = "questions_status")
	private Boolean questionsStatus;
	
	@Column(name="move_to_schedule")
	private Boolean moveToSchedule=false;
	
	@Column(name = "interview_scheduled_datetime")
	private LocalDateTime interviewScheduledDateTime;

	@Column(name="move_to_schedule_datetime")
	private LocalDateTime moveToScheduleDateTime;
	
	@Column(name="interview_link")
	private String interviewLink;


}