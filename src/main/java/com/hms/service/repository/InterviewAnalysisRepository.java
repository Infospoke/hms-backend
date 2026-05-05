package com.hms.service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hms.service.entity.InterviewAnalysisEntity;

@Repository

public interface InterviewAnalysisRepository extends JpaRepository<InterviewAnalysisEntity,Integer>{

	@Query(value = "SELECT DISTINCT application_id FROM tb_interview_analysis WHERE application_id IN (:ids)", nativeQuery = true)
	List<Integer> findInterviewIds(@Param("ids") List<Integer> ids);
	
	@Query("SELECT i.applicationId, i.createdDate " +
		       "FROM InterviewAnalysisEntity i " +
		       "WHERE i.applicationId IN :ids AND i.isDeleted = false")
		List<Object[]> findInterviewDates(@Param("ids") List<Integer> ids);

	long countByJobId(Integer jobId);

	

}
