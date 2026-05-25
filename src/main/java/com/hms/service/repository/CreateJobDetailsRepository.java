package com.hms.service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hms.service.entity.CreateJobDetailsEntity;

@Repository
public interface CreateJobDetailsRepository
		extends JpaRepository<CreateJobDetailsEntity, Integer>, JpaSpecificationExecutor<CreateJobDetailsEntity> {

	Optional<CreateJobDetailsEntity> findByJobCode(String jobCode);

	Long countBySubmitTrue();
	@Query("""
		    SELECT COALESCE(SUM(c.openings), 0)
		    FROM CreateJobDetailsEntity c
		    WHERE c.srId IN (
		         SELECT r.srId
		         FROM RecruiterAssignmentEntity r
		         WHERE r.userId = :userId
		    )
		    """)
		Long getTotalOpeningsByUserId(@Param("userId") Integer userId);

	CreateJobDetailsEntity findBySrId(String srId);
}
