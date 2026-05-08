package com.hms.service.repository;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.hms.service.entity.NotificationEngineEntity;

@Repository
public interface NotificationEngineRepository
		extends JpaRepository<NotificationEngineEntity, Integer>, JpaSpecificationExecutor<NotificationEngineEntity> {

	Long countByIsRead(boolean isRead);
}