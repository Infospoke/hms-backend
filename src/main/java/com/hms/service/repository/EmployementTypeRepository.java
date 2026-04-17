package com.hms.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hms.service.entity.EmployementTypeEntity;

@Repository
public interface EmployementTypeRepository extends JpaRepository<EmployementTypeEntity, Integer> {

}