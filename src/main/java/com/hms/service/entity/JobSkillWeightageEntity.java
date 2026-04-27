package com.hms.service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_job_skill_weightage")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobSkillWeightageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer jobSkillId;

    private Integer jobId;

    private Integer skillId;

    private Integer categoryId;

    private Integer experienceLevel;

    private Integer weightage;

}
