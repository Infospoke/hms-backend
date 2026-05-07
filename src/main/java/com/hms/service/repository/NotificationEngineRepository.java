package com.hms.service.repository;

import com.hms.service.entity.NotificationEngineEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationEngineRepository extends JpaRepository<NotificationEngineEntity, Integer> {
}
