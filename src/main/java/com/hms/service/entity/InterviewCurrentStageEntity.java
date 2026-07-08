package com.hms.service.entity;

import java.time.LocalDate;
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
@Table(name="tb_interview_current_stage")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class InterviewCurrentStageEntity {
	
	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    @Column(name = "id", nullable = false, updatable = false)
	    private Integer id;
	  
	    @Column(name = "interviewer_id")
	    private Integer interviewerId;
	    
	    @Column(name="application_id")
	    private Integer applicationId;

	    
	    @Column(name="current_stage_type")
	    private Integer currentStageType;
	    
	    @Column(name="to_schedule")
	    private Boolean toSchedule;
	    
	    @Column(name="interview_completed")
	    private Boolean interviewCompleted;
	    
	    @Column(name="interview_completed_on")
	    private LocalDateTime interviewCompletedOn;
	    
	    @Column(name="interview_date")
	    private LocalDate interviewDate;

	    
	    @Column(name="start_time")
		private LocalTime startTime;
		
		@Column(name="end_time")
		private LocalTime endTime;

	    
	    @Column(name="feedback")
	    private Boolean feedback;
	    
	    @Column(name="round_order")
	    private Integer roundOrder;
	    
	    @Column(name="created_on")
	    private LocalDate createdOn;
	    
	    @Column(name="feedback_status")
	    private String feedbackStatus;
	  

}
