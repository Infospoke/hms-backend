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
@Table(name = "tb_employement_type")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployementTypeEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Integer id;
    
    @SequenceGenerator(
            name = "emp_seq_gen",
            sequenceName = "emp_seq",
            allocationSize = 1
    )
    
    @Column(name = "employment_id", unique = true)
    private Integer employementId;

    @Column(name = "employement_type")
    private String employementType;

}