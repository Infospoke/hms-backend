package com.hms.service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hms.service.entity.ApprovalsChildEntity;

@Repository
public interface ApprovalsChildRepository extends JpaRepository<ApprovalsChildEntity,Integer>{
	
	@Query("""
			SELECT a FROM ApprovalsChildEntity a
			WHERE a.role1 = :roleId
			   OR a.role2 = :roleId
			   OR a.role3 = :roleId
			""")
			List<ApprovalsChildEntity> findAllByRole(@Param("roleId") Integer roleId);

}
