package com.hms.service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hms.service.entity.PasswordHistoryEntity;
import com.hms.service.enums.CredentialType;

@Repository
public interface PasswordHistoryRepository extends JpaRepository<PasswordHistoryEntity, Integer> {

	 List<PasswordHistoryEntity> findTop5ByUserIdAndCredentialTypeOrderByCreatedAtDesc(
	            Integer userId, CredentialType credentialType);
}
