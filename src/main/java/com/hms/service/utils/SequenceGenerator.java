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
   
    public String generateUserId() {
        Long seq = ((Number) entityManager
                .createNativeQuery("SELECT nextval('user_seq')")
                .getSingleResult()).longValue();

        return "" + seq;
    }
    
    public String generateDepartmentId() {
        Long seq = ((Number) entityManager
                .createNativeQuery("SELECT nextval('dept_seq')")
                .getSingleResult()).longValue();

        return "" + seq;
    }
    
    public String generateBusinessId() {
        Long seq = ((Number) entityManager
                .createNativeQuery("SELECT nextval('bus_seq')")
                .getSingleResult()).longValue();

        return "" + seq;
    }
    
    public String generateEmployementTypeId() {
        Long seq = ((Number) entityManager
                .createNativeQuery("SELECT nextval('emp_seq')")
                .getSingleResult()).longValue();

        return "" + seq;
    }
    
    public String generateUserTypeId() {
        Long seq = ((Number) entityManager
                .createNativeQuery("SELECT nextval('usertype_seq')")
                .getSingleResult()).longValue();

        return "" + seq;
    }
    
    public String generateRoleId() {
        Long seq = ((Number) entityManager
                .createNativeQuery("SELECT nextval('role_seq')")
                .getSingleResult()).longValue();

        return "" + seq;
    }
   
}