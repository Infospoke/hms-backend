package com.hms.service.entity;

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
@Table(name = "tb_departments")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Integer id;
    
    @SequenceGenerator(
            name = "dept_seq_gen",
            sequenceName = "dept_seq",
            allocationSize = 1
    )
    
    @Column(name = "department_id", unique = true)
    private Integer departmentId;
    
    @Column(name = "department_name")
    private String departmentName;

    @Column(name = "business_unit_id")
    private Integer businessUnitId;
    
    @Column(name="department_code")
    private String departmentCode;
}
