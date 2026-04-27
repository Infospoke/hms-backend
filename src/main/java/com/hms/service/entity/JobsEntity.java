package com.hms.service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_jobs")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "job_id", updatable = false)
    private Integer jobId;

    @Column(name = "job_code")
    private String jobCode;

    @Column(name = "job_location")
    private String jobLocation;

    @Column(name = "experience")
    private String experience;

    @Column(name = "job_type")
    private String jobType;

    @Column(name = "job_info")
    private String jobInfo;

    @Column(name = "job_title")
    private String jobTitle;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_date")
    private LocalDateTime createdDate;
    
    @Column(name="job_level")
    private String jobLevel;
    
    @Column(name="job_mode")
    private String jobMode;
    
    @Column(name="job_country")
    private String jobCountry;
    
    @Column(name="is_open")
    private Boolean isOpen;
    
  
}

 