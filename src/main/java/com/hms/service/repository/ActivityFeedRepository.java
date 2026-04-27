package com.hms.service.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hms.service.entity.ActivityFeedEntity;
@Repository
public interface ActivityFeedRepository extends JpaRepository<ActivityFeedEntity, Integer> {
	
	List<ActivityFeedEntity> findAllByTimeStampBetweenOrderByTimeStampDesc(LocalDateTime from, LocalDateTime to);

}
