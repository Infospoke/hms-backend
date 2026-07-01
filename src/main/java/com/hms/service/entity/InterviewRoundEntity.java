package com.hms.service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_interview_round")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InterviewRoundEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Integer roundOrder;

	private String stageName;

	private String stageType;

	private Integer stageTypeId;

	private String interviewMode;

	private Boolean mandatory;

	@ManyToOne
	@JoinColumn(name = "interview_plan_id")
	@lombok.ToString.Exclude
	@lombok.EqualsAndHashCode.Exclude
	private InterviewPlanEntity interviewPlan;

}
