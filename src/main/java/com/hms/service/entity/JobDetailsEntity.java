package com.hms.service.entity;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_job_details")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobDetailsEntity {

 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 @Column(name = "id", nullable = false, updatable = false)
 private Integer id;

 @JoinColumn(name = "job_id")
 private Integer jobId;

 @Column(name = "job_description",length=5000)
 private String jobDescription;

 @Column(name = "job_requirements",length=5000)
 private String jobRequirements;

 @Column(name = "created_by")
 private String createdBy;

 @Column(name = "created_date")
 private LocalDateTime createdDate;

 @Column(name = "skills",length=5000)
 private String skills;

 @Column(name = "qualification")
 private String qualification;

 @Column(name = "updated_by")
 private String updatedBy;

 @Column(name = "updated_date")
 private LocalDateTime updatedDate;

}
 