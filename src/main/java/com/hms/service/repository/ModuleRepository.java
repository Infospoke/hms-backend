package com.hms.service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hms.service.entity.ModuleEntity;


@Repository
public interface ModuleRepository  extends JpaRepository<ModuleEntity, Integer> {

	ModuleEntity findByModuleNameIgnoreCase(String moduleName);

	List<ModuleEntity> findByModuleIdIn(List<Integer> moduleIds);

}
