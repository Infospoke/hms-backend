package com.hms.service.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.hms.service.entity.InterviewCandidateDetailsEntity;

public interface InterviewCandidateDetailsRepository extends JpaRepository<InterviewCandidateDetailsEntity, Integer>,JpaSpecificationExecutor<InterviewCandidateDetailsEntity>{

	long countByUserIdAndCreatedOnBetween(Integer userId, LocalDateTime startDate, LocalDateTime endDate);

	List<InterviewCandidateDetailsEntity> findAllByAssignmentIdAndUserId(Integer id, Integer userId);
	
}
