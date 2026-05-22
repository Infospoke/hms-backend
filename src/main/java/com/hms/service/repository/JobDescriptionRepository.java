package com.hms.service.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hms.service.entity.JobDescriptionEntity;

@Repository
public interface JobDescriptionRepository extends JpaRepository<JobDescriptionEntity, Integer>{

	//Optional<JobDescriptionEntity> findBySr(String srId);

}
