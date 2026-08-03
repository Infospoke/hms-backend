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
@Table(name = "tb_agency_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgencyDetailsEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false, updatable = false)
	private Integer id;

	@Column(name = "agency_name", nullable = false, length = 200)
	private String agencyName;

	@Column(name = "email_id", nullable = false, length = 200)
	private String emailId;

	@Column(name = "category_ids")
	private String categoryIds;
}