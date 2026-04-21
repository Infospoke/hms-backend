package com.hms.service.repository;


import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hms.service.entity.UserEntity;
import com.hms.service.response.UserResponse;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    boolean existsByEmployeeId(Integer employeeId);

    boolean existsByEmail(String email);
    
    @Query("""
    	    SELECT new com.hms.service.response.UserResponse(
    	        u.userId,
    	        u.firstName,
    	        r.roleId,
    	        u.email,
    	        null,
    	        u.active
    	    )
    	    FROM UserEntity u
    	    JOIN AssignRolesEntity r ON u.userId = r.userId
    	    WHERE (:roleId IS NULL OR r.roleId = :roleId)
    	""")
    	Page<UserResponse> findUsersByRole(
    	        @Param("roleId") Integer roleId,
    	        Pageable pageable
    	);
    boolean existsByUsername(String username);
    
    @Query("SELECT COUNT(u) FROM UserEntity u")
    Long getTotalUsers();

   
    @Query("SELECT COUNT(u) FROM UserEntity u WHERE u.active = true")
    Long getActiveUsers();

   
    @Query("SELECT COUNT(u) FROM UserEntity u WHERE u.active = false")
    Long getDeactivatedUsers();

    @Query("""
        SELECT COUNT(u)
        FROM UserEntity u
        JOIN AssignRolesEntity r ON u.userId = r.userId
        WHERE (:roleId IS NULL OR r.roleId = :roleId)
    """)
    Long getFilteredUsers(@Param("roleId") Integer roleId);
    
    Optional<UserEntity> findByUserId(Integer userId);

	UserEntity findByEmail(String email);
}
