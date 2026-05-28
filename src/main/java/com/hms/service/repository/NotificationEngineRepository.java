package com.hms.service.repository;



import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hms.service.entity.NotificationEngineEntity;

@Repository
public interface NotificationEngineRepository
		extends JpaRepository<NotificationEngineEntity, Integer>, JpaSpecificationExecutor<NotificationEngineEntity> {

	Long countByIsRead(boolean isRead);

	Long countByRoleId(Integer roleId);

    Long countByRoleIdAndIsRead(Integer roleId, boolean isRead);
    
    @Query("""
		       SELECT n.id, n.notificationSentAt
		       FROM NotificationEngineEntity n
		       WHERE n.processId = :processId And n.sent=false
		       """)
		List<Object[]> findIdAndSentAtByProcessIdAndSentIsFalse(
		        @Param("processId") String processId);
		
		
		
	List<NotificationEngineEntity> findByProcessIdAndSentIsFalse(String processId);
    
}