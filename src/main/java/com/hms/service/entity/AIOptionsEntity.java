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
@Table(name="tb_ai_options")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AIOptionsEntity {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name ="id",insertable = false , updatable = false)
	private Integer id;
	
	@Column(name="ai_options")
	private String aiOptions;
	

}
