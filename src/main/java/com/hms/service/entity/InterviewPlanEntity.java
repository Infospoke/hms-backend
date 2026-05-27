package com.hms.service.entity;
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
@Table(name="tb_interview_plan")

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InterviewPlanEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false, updatable = false)
	private Integer id;

	@Column(name = "job_id")
	private Integer jobId;
	
	@Column(name="plan_id")
	private Integer planId;
	
	@Column(name="sr_id")
	private String srId;
}
