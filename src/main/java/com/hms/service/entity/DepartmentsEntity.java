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
@Table(name = "tb_departments")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Integer id;
    
    @Column(name = "department_name")
    private String departmentName;

    @Column(name = "business_unit_id")
    private Integer businessUnitId;

    @Column(name="dept_code")
    private String deptCode;
    
    @Column(name="user_departments")
    private Boolean userDepartments;
    
    @Column(name="sr_departments")
    private Boolean srDepartments;

}
