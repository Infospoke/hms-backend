package com.hms.service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hms.service.entity.PermissionEntity;
@Repository
public interface PermissionRepository  extends JpaRepository<PermissionEntity, Integer> {

	List<PermissionEntity> findByRoleId(Integer roleId);

}
