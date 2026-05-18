
package com.hms.service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hms.service.entity.CreateJobEntity;

@Repository
public interface CreateJobRepository extends JpaRepository<CreateJobEntity, Integer> {
	
	Optional<CreateJobEntity> findByJobCode(String jobCode);

}