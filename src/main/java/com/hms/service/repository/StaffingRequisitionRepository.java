package com.hms.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hms.service.entity.SRPositionBasicsEntity;
@Repository
public interface StaffingRequisitionRepository extends JpaRepository<SRPositionBasicsEntity,Integer> {

}
