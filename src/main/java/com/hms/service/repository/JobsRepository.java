package com.hms.service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hms.service.entity.JobsEntity;

@Repository
public interface JobsRepository extends JpaRepository<JobsEntity, Integer> {

	List<JobsEntity> findByJobCountry(String jobCountry);

	JobsEntity findByJobCode(String jobCode);

	Optional<JobsEntity> findByJobId(Integer jobId);

	Optional<JobsEntity> findByJobTitle(String jobTitle);


	@Query("select count(j) from JobsEntity j  where j.jobCode = :jobCode")
	int existsByJobCode(@Param("jobCode") String jobCode);

	long countByIsOpenTrue();

}
