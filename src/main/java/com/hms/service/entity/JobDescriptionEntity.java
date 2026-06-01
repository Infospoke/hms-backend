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
@Table(name = "tb_job_description")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobDescriptionEntity {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "sr_id")
    private String srId;
    
    @Column(name = "job_id")
    private Integer jobId;
    
   
    @Column(name="description",columnDefinition ="TEXT")
    private String description;

}
