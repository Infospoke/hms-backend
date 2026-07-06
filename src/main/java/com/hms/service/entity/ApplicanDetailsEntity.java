package com.hms.service.entity;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_applicant_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicanDetailsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "job_id", nullable = false)
    private Integer jobId;

    @Column(name = "application_id", nullable = false, unique = true)
    private Integer applicationId;

    private String name;

    @Column(columnDefinition = "TEXT")
    private String designation;

    @Column(name = "current_location")
    private String currentLocation;

    @Column(name = "total_experience")
    private String totalExperience;

    private String email;

    @Column(name = "notice_period")
    private String noticePeriod;

    @Column(name = "phone_no")
    private String phoneNo;

    @Column(name = "current_company", columnDefinition = "TEXT")
    private String currentCompany;

    @Column(name = "personal_date_of_birth")
    private String personalDateOfBirth;

    @Column(name = "personal_gender")
    private String personalGender;

    @Column(name = "personal_nationality")
    private String personalNationality;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "personal_languages_known", columnDefinition = "json")
    private List<String> personalLanguagesKnown;

    @Column(name = "personal_address", columnDefinition = "TEXT")
    private String personalAddress;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "education_details", columnDefinition = "json")
    private List<Map<String, Object>> educationDetails;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "experience_details", columnDefinition = "json")
    private List<Map<String, Object>> experienceDetails;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "time_line", columnDefinition = "json")
    private List<String> timeLine;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "company_details", columnDefinition = "json")
    private List<String> companyDetails;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "list_of_experience", columnDefinition = "json")
    private List<String> listOfExperience;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "projects", columnDefinition = "json")
    private List<Map<String, Object>> projects;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "certifications", columnDefinition = "json")
    private List<String> certifications;

    @Column(name = "total_projects_count")
    private Integer totalProjectsCount = 0;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;

        if (this.isDeleted == null) {
            this.isDeleted = false;
        }

        if (this.totalProjectsCount == null) {
            this.totalProjectsCount = 0;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
