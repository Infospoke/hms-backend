package com.hms.service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.hms.service.entity.CreateJobDetailsEntity;

@Repository
public interface CreateJobDetailsRepository
		extends JpaRepository<CreateJobDetailsEntity, Integer>, JpaSpecificationExecutor<CreateJobDetailsEntity> {

	Optional<CreateJobDetailsEntity> findByJobCode(String jobCode);

	Long countBySubmitTrue();

}
