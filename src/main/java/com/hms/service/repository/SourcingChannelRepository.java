package com.hms.service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hms.service.entity.SourcingChannelEntity;
@Repository
public interface SourcingChannelRepository extends JpaRepository<SourcingChannelEntity, Integer> {

	Optional<SourcingChannelEntity> findBySrId(String srId);
}