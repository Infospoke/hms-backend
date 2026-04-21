package com.hms.service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.hms.service.entity.PermissionEntity;
@Repository
public interface PermissionRepository  extends JpaRepository<PermissionEntity, Integer> {


	@Query("""
		    SELECT r.roleId, r.roleName, m.moduleId, m.moduleName,
		           p.create, p.view, p.edit, p.delete
		    FROM PermissionEntity p
		    JOIN RolesEntity r ON p.roleId = r.roleId
		    JOIN ModuleEntity m ON p.moduleId = m.moduleId
		""")
		List<Object[]> fetchPermissionDetails();


	List<PermissionEntity> findByRoleId(Integer roleId);

}
