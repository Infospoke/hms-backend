package com.hms.service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hms.service.entity.AssignRolesEntity;


public interface AssignRolesRepository extends JpaRepository<AssignRolesEntity, Integer> {
	
	Optional<AssignRolesEntity> findByUserId(Integer userId);

	List<AssignRolesEntity> findByRoleId(Integer roleId);


}