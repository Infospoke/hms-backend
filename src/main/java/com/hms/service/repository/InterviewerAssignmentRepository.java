package com.hms.service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hms.service.entity.InterviewerAssignmentEntity;

@Repository
public interface InterviewerAssignmentRepository extends JpaRepository<InterviewerAssignmentEntity, Integer>,
		JpaSpecificationExecutor<InterviewerAssignmentEntity> {

	List<InterviewerAssignmentEntity> findByInterviewerUserId(Long userId);

	Optional<InterviewerAssignmentEntity> findById(Integer planId);

	long countByInterviewerUserId(Integer interviewerUserId);

	List<InterviewerAssignmentEntity> findByJobId(Integer jobId);

	Optional<InterviewerAssignmentEntity> findByJobIdAndStageTypeId(Integer jobId, Integer stageTypeId);
	
	List<InterviewerAssignmentEntity> findByJobIdAndStageTypeIdOrderByIdDesc(Integer jobId, Integer stageTypeId);

	Optional<InterviewerAssignmentEntity> findTopByJobIdAndStageTypeIdOrderByIdDesc(Integer jobId, Integer stageTypeId);
	
	List<InterviewerAssignmentEntity> findByJobIdOrderByIdAsc(Integer jobId);
	
	InterviewerAssignmentEntity findByJobIdAndPlanIdAndStageTypeId(Integer jobId,Integer PlanId, Integer RoundOrder);

	long countByInterviewerUserIdAndRespondedAtIsNull(Long interviewerUserId);
	
	@Query("""
		    SELECT i.status, COUNT(i)
		    FROM InterviewerAssignmentEntity i
		    WHERE i.interviewerUserId = :userId
		    GROUP BY i.status
		    """)
		List<Object[]> getStatusCounts(@Param("userId") Long userId);
}