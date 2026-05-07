package com.hms.service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hms.service.entity.JobApplicationEntity;

@Repository

public interface JobApplicationRepository extends JpaRepository<JobApplicationEntity,Integer>{

	int countByJobId(Integer jobId);

	List<JobApplicationEntity> findByJobIdInOrderByStageEntryDateDesc(List<Integer> jobIds);

	List<JobApplicationEntity> findByJobIdOrderByCreatedDateDesc(Integer jobId);

}
