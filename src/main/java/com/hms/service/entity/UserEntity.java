package com.hms.service.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.hms.service.enums.UserStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
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

    
    @SequenceGenerator(
            name = "user_seq_gen",
            sequenceName = "user_seq",
            allocationSize = 1
    )
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

    @Column(name = "mobile_number", nullable = false)
    private String mobileNumber;

    @Column(name = "alternate_contact")
    private String alternateContact;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "employment_type_id", nullable = false)
    private Integer employmentTypeId;

    @Column(name = "business_unit_id", nullable = false)
    private Integer businessUnitId;

    @Column(name = "department_id", nullable = false)
    private Integer departmentId;

    @Column(name = "role_id", nullable = false)
    private Integer roleId;
    
    @Enumerated(EnumType.STRING)
    private UserStatus status;
    
    @Column(name = "password")
    private String password; 
    
    @Column(name = "pin")
    private String pin;     

    @Column(name = "created_by")
    private String createdBy;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_by")
    private String updatedBy;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}