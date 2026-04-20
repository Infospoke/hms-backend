package com.hms.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hms.service.entity.BusinessUnitEntity;

@Repository
public interface BusinessUnitRepository extends JpaRepository<BusinessUnitEntity, Integer> {
}