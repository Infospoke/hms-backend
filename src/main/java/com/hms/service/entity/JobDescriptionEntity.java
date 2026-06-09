package com.hms.service.entity;

import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.hms.service.request.Description;
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
    
   
    @JdbcTypeCode(SqlTypes.JSON)

   	@Column(name = "description", columnDefinition = "json")

   	private List<Description> description;

}
