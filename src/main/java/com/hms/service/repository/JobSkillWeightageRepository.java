package com.hms.service.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hms.service.entity.JobSkillWeightageEntity;



@Repository
public interface JobSkillWeightageRepository
        extends JpaRepository<JobSkillWeightageEntity, Integer> {
	
	void deleteByJobId(Integer jobId);

	JobSkillWeightageEntity findByJobIdAndSkillId(Integer jobId, Integer skillId);

	List<JobSkillWeightageEntity> findByJobId(Integer jobId);
}
