package com.hms.service.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.hms.service.entity.InterviewCurrentStageEntity;

@Repository

public interface InterviewCurrentStageRepository extends JpaRepository<InterviewCurrentStageEntity, Integer>,
		JpaSpecificationExecutor<InterviewCurrentStageEntity> {

	InterviewCurrentStageEntity findByApplicationIdAndToScheduleFalse(Integer applicationId);

	Integer countByApplicationId(Integer applicationId);

	InterviewCurrentStageEntity findByApplicationIdAndFeedbackFalse(Integer applicantId);

	Page<InterviewCurrentStageEntity> findAll(Specification<InterviewCurrentStageEntity> todayInterviewSpecification,
			Pageable pageable);

	InterviewCurrentStageEntity findByApplicationId(Integer applicationId);

	List<InterviewCurrentStageEntity> findByApplicationIdIn(List<Integer> applicationIds);

	List<InterviewCurrentStageEntity> findByFeedbackFalseAndInterviewCompletedTrue();
	
	InterviewCurrentStageEntity findByApplicationIdAndInterviewDate(Integer applicationId, LocalDate now);
 
	
	List<InterviewCurrentStageEntity> findByToScheduleFalse();
 
	Page<InterviewCurrentStageEntity> findByToScheduleFalse(Pageable pageable);
 
	List<InterviewCurrentStageEntity> findAll(Specification<InterviewCurrentStageEntity> specification);
 

}
