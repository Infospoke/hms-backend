package com.hms.service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hms.service.entity.SourcingChannelEntity;
@Repository
public interface SourcingChannelRepository extends JpaRepository<SourcingChannelEntity, Integer> {

	List<SourcingChannelEntity> findByJobId(Integer jobId);
}