package com.hms.service.repository;




import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hms.service.entity.JobDetailsEntity;


@Repository
public interface JobDetailsRepository extends JpaRepository<JobDetailsEntity, Integer> {

	JobDetailsEntity findByJobId(Integer id);

	void deleteByJobId(Integer jobId);

}