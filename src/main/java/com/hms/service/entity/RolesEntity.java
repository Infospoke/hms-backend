package com.hms.service.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_role")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RolesEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false, updatable = false)
	private Integer id;

	@SequenceGenerator(name = "role_seq_gen",sequenceName = "role_seq", allocationSize = 1)
	@Column(name = "role_id",nullable=false, unique = true)
	private Integer roleId;

	@Column(name = "role_name",unique=true)
	private String roleName;

	@Column(name = "department_id")
	private Integer departmentId;

	@Column(name = "created_date")
	private LocalDate createdDate;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "updated_by")
	private String updatedBy;

	@Column(name = "updated_date")
	private LocalDate updatedDate;

	@Column(name = "business_unit_id")
	private Integer businessUnitId;

	@Column(name = "description")
	private String description;
}