package com.hms.service.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import com.hms.service.entity.ResumeAnalysisEntity;

@Repository
public interface ResumeAnalysisRepository extends JpaRepository<ResumeAnalysisEntity,Integer>{
	
	@Query("SELECT r.applicationId FROM ResumeAnalysisEntity r WHERE r.applicationId IN :ids")
	List<Integer> findScreenedIds(@Param("ids") List<Integer> applicationIds);

	long countByJobId(Integer jobId);

	long countByJobIdAndStatusIgnoreCase(Integer jobId, String status);

	@Query("SELECT r.applicationId, r.status FROM ResumeAnalysisEntity r WHERE r.applicationId IN :ids")
	List<Object[]> findScreenStatuses(@Param("ids") List<Integer> applicationIds);

	@Query("SELECT r.applicationId, r.status, r.success, r.createdAt " +
		       "FROM ResumeAnalysisEntity r " +
		       "WHERE r.applicationId IN :ids")

		List<Object[]> findResumeDetails(@Param("ids") List<Integer> ids);

		List<ResumeAnalysisEntity> findByApplicationIdIn(List<Integer> applicationIds);

		Optional<ResumeAnalysisEntity> findByApplicationId(Integer applicationId);

}
