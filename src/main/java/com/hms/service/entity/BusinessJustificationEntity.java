package com.hms.service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@Table(name="tb_sr_business_justification")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BusinessJustificationEntity {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id",updatable=false,nullable=false)
	private Integer id;
	
	@Column(name = "sr_id", unique = true, nullable = false)
	private String srId;
	
	@Column(name="requisition_type")
	private String requisitionType;
	
	@Column(name="business_case",length = 2000)
	private String businessCase;
	
	@Column(name="impact_if_not_filled",length=2000)
	private String impactIfNotFilled;
	
	@Column(name="replaces_employee")
	private Integer replacesEmployee;
	
	@Column(name="document")
	private String document;
	
	@Column(name="draft")
	private Boolean draft;
	
	@Column(name="submitted")
	private Boolean submitted;
	
	@Column(name="approved")
	private Boolean approved;
	
	

}
