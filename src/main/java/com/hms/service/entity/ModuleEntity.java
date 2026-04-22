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
@Table(name = "tb_module")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class ModuleEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false, updatable = false)
	private Integer id;

    
    @SequenceGenerator(
            name = "module_seq_gen",
            sequenceName = "module_seq",
            allocationSize = 1
    )    
	@Column(name = "module_id",nullable=false)
    private Integer moduleId;
    
    @Column(name="parent_id",nullable=false)
    private Integer parentId;
	
	@Column(name = "module_name")
	private String moduleName;

	@Column(name = "created_date")
	private LocalDate createdDate;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "updated_by")
	private String updatedBy;

	@Column(name = "updated_date")
	private LocalDate updatedDate;

}
