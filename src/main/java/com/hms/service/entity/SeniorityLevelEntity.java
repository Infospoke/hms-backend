package com.hms.service.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tb_seniority_level")
@Data
public class SeniorityLevelEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "seniority_level")
	private String seniorityLevel;


}
