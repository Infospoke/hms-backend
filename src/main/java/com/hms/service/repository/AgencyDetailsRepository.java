package com.hms.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.hms.service.entity.AgencyDetailsEntity;
@Repository
public interface AgencyDetailsRepository
		extends JpaRepository<AgencyDetailsEntity, Integer>, JpaSpecificationExecutor<AgencyDetailsEntity> {

}