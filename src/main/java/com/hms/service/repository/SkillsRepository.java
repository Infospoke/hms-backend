package com.hms.service.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hms.service.entity.SkillsEntity;


@Repository
public interface SkillsRepository extends JpaRepository<SkillsEntity, Integer> {

	@Query("SELECT s.id, s.skillName FROM SkillsEntity s WHERE s.id IN :ids")
	List<Object[]> findSkillNamesByIds(@Param("ids") List<Integer> ids);
}