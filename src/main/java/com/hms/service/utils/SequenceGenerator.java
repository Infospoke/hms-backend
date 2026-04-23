package com.hms.service.utils;

import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Configuration
public class SequenceGenerator {

    @PersistenceContext
    private EntityManager entityManager;

    @PostConstruct
    public void init() {
        try {
            entityManager.createNativeQuery(
                "CREATE SEQUENCE IF NOT EXISTS user_seq START 1"
            ).executeUpdate();
        } catch (Exception e) {
            // ignore if already exists
        }
    }

    public Integer generateUserId() {
        return ((Number) entityManager
                .createNativeQuery("SELECT nextval('user_seq')")
                .getSingleResult()).intValue();
    }


    public Integer generateRoleId() {
        return ((Number) entityManager
                .createNativeQuery("SELECT nextval('role_seq')")
                .getSingleResult()).intValue();
    }

    public Integer generateModuleId() {
        return ((Number) entityManager
                .createNativeQuery("SELECT nextval('module_seq')")
                .getSingleResult()).intValue();
    }
    
    public Integer generatePermissionId() {
        return ((Number) entityManager
                .createNativeQuery("SELECT nextval('permission_seq')")
                .getSingleResult()).intValue();
    }

	 public Integer generateAssignRoleId() {
        return ((Number) entityManager
                .createNativeQuery("SELECT nextval('permission_seq')")
                .getSingleResult()).intValue();
    }
	 
	 public Integer generateSrSequence() {
	        return ((Number) entityManager
	                .createNativeQuery("SELECT nextval('sr_seq')")
	                .getSingleResult()).intValue();
	    }
}