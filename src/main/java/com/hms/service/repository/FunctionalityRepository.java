package com.hms.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hms.service.entity.FunctionalityEntity;

@Repository
public interface FunctionalityRepository extends JpaRepository<FunctionalityEntity,Integer>{

}
