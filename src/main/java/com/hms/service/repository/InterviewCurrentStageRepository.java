package com.hms.service.repository;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hms.service.entity.InterviewCurrentStageEntity;

@Repository
public interface InterviewCurrentStageRepository extends JpaRepository<InterviewCurrentStageEntity,Integer>{
	
	InterviewCurrentStageEntity  findByApplicationIdAndToScheduleFalse(Integer applicationId);

	InterviewCurrentStageEntity findByApplicationIdAndInterviewDate(Integer applicationId, LocalDate now);

	Integer countByApplicationId(Integer applicationId);
	 

}
