package com.hms.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hms.service.entity.CreateJobDetailsEntity;
@Repository
public interface CreateJobDetailsRepository extends JpaRepository<CreateJobDetailsEntity, Integer> {

}
