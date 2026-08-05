package com.hms.service.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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
	
	long countByIsOpenTrue();

	Page<CreateJobDetailsEntity> findAll(Specification<CreateJobDetailsEntity> spec, Pageable pageable);

	CreateJobDetailsEntity findByJobCode(Integer jobId);

	List<CreateJobDetailsEntity> findByIsOpenTrue(Sort sort);

	Optional<CreateJobDetailsEntity> findByJobTitle(String jobTitle);

	List<CreateJobDetailsEntity> findByCountry(String country);

	CreateJobDetailsEntity findByJobId(Integer jobId);

	List<CreateJobDetailsEntity> findByJobIdIn(List<Integer> jobIds);
	
	List<CreateJobDetailsEntity> findByJobIdIn(Set<Integer> jobIds);

	

	
}
