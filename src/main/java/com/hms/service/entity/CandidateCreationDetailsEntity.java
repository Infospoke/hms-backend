package com.hms.service.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "tb_candidate_creation")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CandidateCreationDetailsEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false, updatable = false)
	private Integer id;

	@Column(name = "first_name")
	private String firstName;

	@Column(name = "last_name")
	private String lastName;

	@Column(name = "phone_number")
	private String phoneNumber;

	@Column(name = "email")
	private String email;

	@Column(name = "password")
	private String password;

	@Column(name = "resume")
	private String resume;

	@Column(name = "additional_file")
	private String additionalFile;

	@Column(name = "candidate_id", unique = true)
	private String candidateId;

	@Column(name = "failed_attempts")
	private Integer failedAttempts = 0;

	@Column(name = "account_locked")
	private Boolean accountLocked = false;

	@Column(name = "lock_time")
	private LocalDateTime lockTime;

	@Column(name = "password_updated_at")
	private LocalDateTime passwordUpdatedAt;

	@Column(name = "token", columnDefinition = "TEXT")
	private String token;

	@Column(name = "is_logged_in")
	private Boolean loggedIn = false;

	@Column(name = "last_login")
	private LocalDateTime lastLogin;

	@Column(name = "last_logout")
	private LocalDateTime lastLogout;

	@Column(name = "force_password_reset")
	private Boolean forcePasswordReset = false;

	@Column(name = "temporary_password")
	private Boolean temporaryPassword = false;

	@Column(name = "temporary_password_expiry")
	private LocalDateTime temporaryPasswordExpiry;


}
