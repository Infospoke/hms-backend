package com.hms.service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_interview_sessions")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InterviewSessionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "application_id", nullable = false)
    private Integer applicationId;

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

    @Column(name = "schedule_email_sent")
    private Boolean scheduleEmailSent = false;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @Column(name = "exam_exit_password", length = 255)
    private String examExitPassword;
    
    @Column(name="status")
	private String status;
}