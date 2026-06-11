package com.hms.service.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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
@Table(name = "tb_ai_interview_questions")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AIInterviewQuestionsEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "number_of_questions", nullable = false)
	private Integer numberOfQuestions;

	@Column(name = "difficulty_level", length = 50)
	private String difficultyLevel;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "question_type", columnDefinition = "jsonb")
	private List<String> questionType;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "questions", columnDefinition = "jsonb")
	private List<Object> questions;

	@CreationTimestamp
	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "application_id")
	private Integer applicationId;
}