package com.hms.service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hms.service.entity.InterviewSessionEntity;

@Repository
public interface InterviewSessionRepository
		extends JpaRepository<InterviewSessionEntity, Integer>, JpaSpecificationExecutor<InterviewSessionEntity> {

	@Query("SELECT i.applicationId, i.status FROM InterviewSessionEntity i WHERE i.applicationId IN :applicationIds AND (i.isDeleted = false OR i.isDeleted IS NULL)")
	List<Object[]> findApplicationIdAndStatus(@Param("applicationIds") List<Integer> applicationIds);
}