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
@Table(name = "tb_business_unit")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BusinessUnitEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Integer id;
    
    @SequenceGenerator(
            name = "bus_seq_gen",
            sequenceName = "bus_seq",
            allocationSize = 1
    )
    @Column(name = "business_id", unique = true)
    private Integer businessId;

    @Column(name = "business_name")
    private String businessName;
}

