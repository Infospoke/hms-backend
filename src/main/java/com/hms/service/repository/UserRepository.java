package com.hms.service.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hms.service.entity.UserEntity;
import com.hms.service.enums.UserStatus;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    boolean existsByEmployeeId(Integer employeeId);

    boolean existsByEmail(String email);
    
    Page<UserEntity> findByFirstNameContainingIgnoreCase(String name, Pageable pageable);

    long countByStatus(UserStatus status);
    
    long countByRoleId(Integer roleId);

	UserEntity findByEmail(String email);
}
