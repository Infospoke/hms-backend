package com.hms.service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hms.service.entity.RolesEntity;


@Repository
public interface RolesRepository extends JpaRepository<RolesEntity, Integer> {

    List<RolesEntity> findByDepartmentId(Integer departmentId, Sort sort);

	RolesEntity findByRoleNameIgnoreCase(String roleName);

	Optional<RolesEntity> findByRoleId(Integer roleId);

	boolean existsByRoleIdAndDepartmentId(Integer roleId, Integer departmentId);
	
	@Query("""
		    SELECT r.roleId, r.roleName
		    FROM RolesEntity r
		    WHERE r.roleId IN :roleIds
		""")
		List<Object[]> findRoleNamesByIds(
		        @Param("roleIds") List<Integer> roleIds);

	List<RolesEntity> findByDepartmentIdIn(List<Integer> departmentIds);


	
}