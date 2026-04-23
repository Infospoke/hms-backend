package com.hms.service.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_user")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Integer id;

    @SequenceGenerator(name = "user_seq_gen", sequenceName = "user_seq", allocationSize = 1)
    @Column(name = "user_id", unique = true, nullable = false)
    private Integer userId;

    @Column(name = "user_type_id", nullable = false)
    private Integer userTypeId;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(name = "employee_id", nullable = false, unique = true, updatable = false)
    private Integer employeeId;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Pattern(regexp = "^[0-9]{10,15}$")
    @Column(name = "mobile_number", nullable = false,unique = true, length = 15)
    private String mobileNumber;

    @Pattern(regexp = "^[0-9]{10,15}$")
    @Column(name = "alternate_contact",unique = true, length = 15)
    private String alternateContact;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "employment_type_id", nullable = false)
    private Integer employmentTypeId;

    @Column(name = "business_unit_id", nullable = false)
    private Integer businessUnitId;

    @Column(name = "department_id", nullable = false)
    private Integer departmentId;

    @Column(name = "password")
    private String password;

    @Column(name = "pin")
    private String pin;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "updated_at")
    private LocalDate updatedAt;

    @Column(name = "role_name",unique=true)
    private String roleName;

    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @Column(name = "deactivated", nullable = false)
    private Boolean deactivated = false;

	 @Column(name = "username")
    private String username;
	 
	 @Column(name = "failed_attempts")
	 private Integer failedAttempts = 0;

	 @Column(name = "account_locked")
	 private Boolean accountLocked = false;

	 @Column(name = "lock_time")
	 private LocalDateTime lockTime;
	 
	 private Boolean forcePasswordReset = false;

	 private LocalDateTime passwordUpdatedAt;

}