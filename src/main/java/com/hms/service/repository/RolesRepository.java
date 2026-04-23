package com.hms.service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hms.service.entity.RolesEntity;
import com.hms.service.entity.UserEntity;

@Repository
public interface RolesRepository extends JpaRepository<RolesEntity, Integer> {

    List<RolesEntity> findByDepartmentId(Integer departmentId, Sort sort);

	RolesEntity findByRoleNameIgnoreCase(String roleName);

	Optional<RolesEntity> findByRoleId(Integer roleId);
}