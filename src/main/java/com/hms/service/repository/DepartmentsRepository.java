package com.hms.service.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hms.service.entity.DepartmentsEntity;

@Repository
public interface DepartmentsRepository extends JpaRepository<DepartmentsEntity, Integer> {

	List<DepartmentsEntity> findByBusinessUnitId(Integer businessUnitId, Sort sort);
	
}