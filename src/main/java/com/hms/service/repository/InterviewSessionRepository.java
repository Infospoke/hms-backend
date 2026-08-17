package com.hms.service.repository;

import java.util.List;

import java.util.Optional;

import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Repository;
import com.hms.service.entity.InterviewSessionEntity;

@Repository
public interface InterviewSessionRepository
		extends JpaRepository<InterviewSessionEntity, Integer>, JpaSpecificationExecutor<InterviewSessionEntity> {

	@Query("SELECT i.applicationId, i.status FROM InterviewSessionEntity i WHERE i.applicationId IN :applicationIds AND (i.isDeleted = false OR i.isDeleted IS NULL)")
	List<Object[]> findApplicationIdAndStatus(@Param("applicationIds") List<Integer> applicationIds);

	List<InterviewSessionEntity> findByJobId(Integer jobId);
	
	Optional<InterviewSessionEntity> findByApplicationId(Integer applicationId);
	 

	@Query("""
			 SELECT i.jobId, COUNT(i)
			 FROM InterviewSessionEntity i
			 WHERE LOWER(i.status) = 'completed'
			 GROUP BY i.jobId
			""")
	List<Object[]> getCompletedInterviewCountByJobId();

	Optional<InterviewSessionEntity> findFirstByApplicationIdAndStatusIgnoreCase(Integer applicationId, String status);

	List<InterviewSessionEntity> findByApplicationIdIn(List<Integer> applicationIds);

	List<InterviewSessionEntity> findByStatusIgnoreCase(String status);

}