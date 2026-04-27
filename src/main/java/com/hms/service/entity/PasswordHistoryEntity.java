package com.hms.service.entity;

import java.time.LocalDateTime;

import com.hms.service.enums.CredentialType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_password_history")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "credential", nullable = false)
    private String credential;

    @Enumerated(EnumType.STRING)
    @Column(name = "credential_type", nullable = false)
    private CredentialType credentialType;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

}
