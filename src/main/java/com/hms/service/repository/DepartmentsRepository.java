package com.hms.service.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hms.service.entity.DepartmentsEntity;

@Repository
public interface DepartmentsRepository extends JpaRepository<DepartmentsEntity, Integer> {

	List<DepartmentsEntity> findByBusinessUnitId(Integer businessUnitId, Sort sort);

	Optional<DepartmentsEntity> findByBusinessUnitId(Integer businessUnitId);

	@Query("SELECT d.deptCode FROM DepartmentsEntity d WHERE d.businessUnitId = :businessUnitId")
	String findDeptCodeByBusinessUnitId(@Param("businessUnitId") Integer businessUnitId);

	boolean existsByIdAndBusinessUnitId(Integer departmentId, Integer businessUnitId);

	Optional<DepartmentsEntity> findById(Integer id);

	List<DepartmentsEntity> findByIdIn(Collection<Integer> ids);
	
    List<DepartmentsEntity> findByUserDepartmentsTrue();

	List<DepartmentsEntity> findBySrDepartmentsTrue();
	
}
