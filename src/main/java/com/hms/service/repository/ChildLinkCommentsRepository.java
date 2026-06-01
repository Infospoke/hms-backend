package com.hms.service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hms.service.entity.ChildLinkCommentsEntity;

@Repository
public interface ChildLinkCommentsRepository extends JpaRepository<ChildLinkCommentsEntity,Integer>{

	List<ChildLinkCommentsEntity> findByChainId(Integer id);

	List<ChildLinkCommentsEntity> findByPlanIdOrderByCreatedAtAsc(Integer id);

}
