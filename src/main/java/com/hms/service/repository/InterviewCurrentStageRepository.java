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

	Page<InterviewCurrentStageEntity> findAll(Specification<InterviewCurrentStageEntity> todayInterviewSpecification,
			Pageable pageable);

	List<InterviewCurrentStageEntity> findByApplicationIdIn(List<Integer> applicationIds);

	List<InterviewCurrentStageEntity> findByFeedbackFalseAndInterviewCompletedTrue();
	
	InterviewCurrentStageEntity findByApplicationIdAndInterviewDate(Integer applicationId, LocalDate now);
 
	 
	InterviewCurrentStageEntity  findByApplicationIdAndFeedbackFalse(Integer applicationId);
	
	List<InterviewCurrentStageEntity> findByToScheduleFalse();

	Page<InterviewCurrentStageEntity> findByToScheduleFalse(Pageable pageable);

	List<InterviewCurrentStageEntity> findAll(Specification<InterviewCurrentStageEntity> specification);

	long countByInterviewerIdAndInterviewDate(Integer interviewerId, LocalDate interviewDate);

	long countByInterviewerIdAndToScheduleFalse(Integer interviewerId);

	long countByInterviewerIdAndToScheduleTrueAndInterviewCompletedFalse(Integer interviewerId);

	long countByInterviewerIdAndInterviewCompletedTrueAndFeedbackFalse(Integer interviewerId);

	InterviewCurrentStageEntity findByApplicationIdAndToScheduleFalse(String valueOf);

	InterviewCurrentStageEntity findByApplicationIdAndCurrentStageType(Integer applicationId, Integer roundId);
	
	InterviewCurrentStageEntity findTopByApplicationIdOrderByIdDesc(Integer applicationId);
	
	List<InterviewCurrentStageEntity> findByApplicationId(Integer applicationId);

	List<InterviewCurrentStageEntity> findByApplicationIdOrderByRoundOrder(Integer id);
	



}


