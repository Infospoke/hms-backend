package com.hms.service.repository;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.hms.service.entity.StaffingRequisitionEntitys;

@Repository
public interface Staffing extends MongoRepository<StaffingRequisitionEntitys,String> {
	
	Integer findTopByOrderByIdDesc();

}
