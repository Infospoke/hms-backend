package com.hms.service.entity;
 
import java.time.LocalDate;
import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.hms.service.request.LevelConfig;

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

@Table(name = "tb_chain_approval")

@Data

@AllArgsConstructor

@NoArgsConstructor

public class ApprovalChainEntity {
 
	@Id

	@GeneratedValue(strategy = GenerationType.IDENTITY)

	@Column(name="id",updatable=false,nullable=false)

	private Integer id;
 
	@Column(name = "chain_name", nullable = false,unique=true)

	private String chainName;
 
	@Column(name = "description")

	private String description;
 
	@Column(name = "status")

	private String status;

	@Column(name="functionality")

	private Integer functionality;

	@Column(name="created_at")

	private LocalDate createdAt;

	@Column(name="created_by")

	private String createdBy;

	@Column(name = "updated_by")

	private String updatedBy;
 
	@Column(name = "updated_at")

	private LocalDate updatedAt;

	@Column(name="approval")

	private String approval;

	@Column(name="approved_comments",length=300)

	private String approvedComments;

	@Column(name="rejected_comments",length=300)

	private String rejectedComments;

    @JdbcTypeCode(SqlTypes.JSON)

	@Column(name = "level_config", columnDefinition = "json")

	private List<LevelConfig> levelConfig;

}
