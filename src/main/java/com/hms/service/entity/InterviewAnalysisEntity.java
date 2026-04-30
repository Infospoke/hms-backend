package com.hms.service.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.hms.service.enums.StatusEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="tb_interview_analysis")

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InterviewAnalysisEntity {
	
	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Integer id;

	    @Column(name = "application_id", nullable = false)
	    private Integer applicationId;
	   

	    @Column(name = "interview_session_id", nullable = false)
	    private String interviewSessionId;

	    @Enumerated(EnumType.STRING)
	    @Column(name = "status")
	    private StatusEnum status = StatusEnum.NOT_STARTED;

	    @JdbcTypeCode(SqlTypes.JSON)
	    @Column(name = "questions", columnDefinition = "json")
	    private List<String> questions;
	    
	    @Column(name = "total_score")
	    private Double totalScore = 0.0;

	    @Column(name = "recommendation", length = 20)
	    private String recommendation;

	    @Column(name = "analysis_completed")
	    private Boolean analysisCompleted = false;

	    @Column(name = "email_sent")
	    private Boolean emailSent = false;

	    @Column(name = "is_deleted")
	    private Boolean isDeleted = false;
	    
	    @Column(name="job_id")
	    private Integer jobId;

	    @Column(name = "created_date")
	    private LocalDateTime createdDate;


}
