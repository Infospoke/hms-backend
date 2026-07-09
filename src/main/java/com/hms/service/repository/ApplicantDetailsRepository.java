package com.hms.service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.hms.service.entity.ApplicanDetailsEntity;

@Repository
public interface ApplicantDetailsRepository
		extends JpaRepository<ApplicanDetailsEntity, Integer>, JpaSpecificationExecutor<ApplicanDetailsEntity> {

	Optional<ApplicanDetailsEntity> findByApplicationId(Integer applicationId);

	Page<ApplicanDetailsEntity> findByApplicationIdIn(List<Integer> applicationIds, Pageable pageable);

}
