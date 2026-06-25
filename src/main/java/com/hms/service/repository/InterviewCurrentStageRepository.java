package com.hms.service.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hms.service.entity.InterviewCandidateDetailsEntity;
import com.hms.service.entity.InterviewCurrentStageEntity;

@Repository
public interface InterviewCurrentStageRepository extends JpaRepository<InterviewCurrentStageEntity,Integer>{
	
	InterviewCurrentStageEntity  findByApplicationIdAndToScheduleFalse(Integer applicationId);

	InterviewCurrentStageEntity findByApplicationIdAndInterviewDate(Integer applicationId, LocalDate now);

	Integer countByApplicationId(Integer applicationId);
	 
	InterviewCurrentStageEntity  findByApplicationIdAndFeedbackFalse(Integer applicationId);
	
	List<InterviewCurrentStageEntity> findByToScheduleFalse();

	Page<InterviewCurrentStageEntity> findByToScheduleFalse(Pageable pageable);

	List<InterviewCurrentStageEntity> findAll(Specification<InterviewCurrentStageEntity> specification);



}
