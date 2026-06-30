package com.hms.service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.hms.service.entity.InterviewerAssignmentEntity;
import com.hms.service.request.UpdateInterviewFeedbackRequest;

@Repository
public interface InterviewerAssignmentRepository extends JpaRepository<InterviewerAssignmentEntity, Integer>,
		JpaSpecificationExecutor<InterviewerAssignmentEntity> {

	List<InterviewerAssignmentEntity> findByInterviewerUserId(Long userId);

	Optional<InterviewerAssignmentEntity> findById(Integer planId);

	long countByInterviewerUserId(Integer interviewerUserId);

	List<InterviewerAssignmentEntity> findByJobId(Integer jobId);

	Optional<InterviewerAssignmentEntity> findByJobIdAndRoundId(Integer jobId, Long id);
	
	List<InterviewerAssignmentEntity> findByJobIdAndRoundIdOrderByIdDesc(Integer jobId, Long roundId);

	Optional<InterviewerAssignmentEntity> findTopByJobIdAndRoundIdOrderByIdDesc(Integer jobId, Long roundId);
	
	List<InterviewerAssignmentEntity> findByJobIdOrderByIdAsc(Integer jobId);
	
	InterviewerAssignmentEntity findByJobIdAndPlanIdAndStageTypeId(Integer jobId,Integer PlanId, Integer RoundOrder);

	long countByInterviewerUserIdAndRespondedAtIsNull(Long interviewerUserId);
}