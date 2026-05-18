
package com.hms.service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hms.service.entity.CreateJob;

@Repository
public interface CreateJobRepository extends JpaRepository<CreateJob, Integer> {
	
	Optional<CreateJob> findByJobCode(String jobCode);

}