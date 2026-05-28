package com.hms.service.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_interview_plan")

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InterviewPlanEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false, updatable = false)
	private Integer id;

	private String planName;

	private String description;

	private String status;

	private String createdBy;

	private LocalDateTime createdOn;

	@OneToMany(mappedBy = "interviewPlan", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<InterviewRoundEntity> rounds;
}
