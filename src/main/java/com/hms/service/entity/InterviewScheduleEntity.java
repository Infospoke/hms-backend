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
@Table(name = "tb_interview_schedule")

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InterviewScheduleEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false, updatable = false)
	private Integer id;
	
	@Column(name="user_id")
	private Integer userId;
	
	@Column(name="applicant_id")
	private Integer applicantId;
	
	@Column(name="round_type")
	private String roundType;
	
	@Column(name="interview_date")
	private LocalDate interviewDate;
	
	@Column(name="start_time")
	private LocalTime startTime;
	
	@Column(name="end_time")
	private LocalTime endTime;
	
	@Column(name="interview_type")
	private String interviewType;
	
	@Column(name="meeting_link")
	private String meetingLink;
	
	@Column(name="venue_details")
	private String venueDetails;
	
	@Column(name="created_by")
	private String createdBy;
	
	@Column(name="created_on")
	private LocalDateTime createdOn;
	
	@Column(name="updated_by")
	private String updatedBy;
	
	@Column(name="updated_date")
	private LocalDateTime updateOn;
	

}
