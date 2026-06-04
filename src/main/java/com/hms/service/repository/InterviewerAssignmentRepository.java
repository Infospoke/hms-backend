package com.hms.service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.hms.service.entity.InterviewerAssignmentEntity;

@Repository
public interface InterviewerAssignmentRepository extends JpaRepository<InterviewerAssignmentEntity, Integer>,
		JpaSpecificationExecutor<InterviewerAssignmentEntity> {

	List<InterviewerAssignmentEntity> findByInterviewerUserId(Long userId);

	Optional<InterviewerAssignmentEntity> findById(Integer planId);
	
	 long countByInterviewerUserId(Integer interviewerUserId);

	List<InterviewerAssignmentEntity> findByPlanId(Integer planId);

	Optional<InterviewerAssignmentEntity> findByJobIdAndRoundId(Integer jobId, Long id);
}