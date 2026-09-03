package com.hms.service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hms.service.entity.ClientManagementDetailsEntity;


@Repository
public interface ClientRepository extends JpaRepository<ClientManagementDetailsEntity, Integer> {

	Page<ClientManagementDetailsEntity> findAll(Specification<ClientManagementDetailsEntity> clientSpecification,
			Pageable pageable);


	

}
