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
@Table(name="tb_interview_feedback")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InterviewFeedbackEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false, updatable = false)
	private Integer id;
	
	@Column(name="applicant_id")
	private Integer applicantId;
	
	@Column(name="interview_type")
	private String interviewType;
	
	@Column(name="round_type")
	private String roundType;
	
	@Column(name="overall_rating")
	private Integer overallRating;
	
	@Column(name="technical_knowledge")
	private Integer technicalKnowledge;
	
	@Column(name="communication")
	private Integer communication;
	
	@Column(name="problem_solving")
	private Integer problemSolving;
	
	@Column(name="analytical_thinking")
	private Integer analyticalThinking;
	
	@Column(name="cultural_fit")
	private Integer culturalFit;
	
	@Column(name="strengths")
	private String strengths;
	
	@Column(name="areas_of_improvements")
	private String areasOfImprovemnets;
	
	@Column(name="additional_comments")
	private String additionalComments;
	
	@Column(name="decision")
	private String decision;
	
	@Column(name="submitted_on")
	private LocalDateTime submittedOn;
	
	@Column(name="submitted_by")
	private Integer submittedBy;

}
