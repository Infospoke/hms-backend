package com.hms.service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hms.service.entity.AssignRolesEntity;

@Repository
public interface AssignRolesRepository extends JpaRepository<AssignRolesEntity, Integer> {
	
	Optional<AssignRolesEntity> findByUserId(Integer userId);

	List<AssignRolesEntity> findByRoleId(Integer roleId);
	
	@Query("""
		    SELECT ar.roleId, COUNT(DISTINCT ar.userId)
		    FROM AssignRolesEntity ar
		    WHERE ar.roleId IN :roleIds
		    GROUP BY ar.roleId
		""")
		List<Object[]> countUsersByRoleIds(@Param("roleIds") List<Integer> roleIds);


}