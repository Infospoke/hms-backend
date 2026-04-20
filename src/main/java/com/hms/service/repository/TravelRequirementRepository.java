package com.hms.service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.hms.service.entity.TravelRequirementEntity;

@Repository
public interface TravelRequirementRepository extends JpaRepository<TravelRequirementEntity, Long> {

}

