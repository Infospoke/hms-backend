package com.hms.service.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_resume_analysis")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResumeAnalysisEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "application_id", unique = true)
    private Integer applicationId;

    @Column(name = "candidate_name", length = 150)
    private String candidateName;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "contact_number", length = 50)
    private String contactNumber;

    @Column(name = "final_score")
    private Double finalScore;

    @Column(name = "skills_match")
    private Double skillsMatch;

    @Column(name = "experience_score")
    private Double experienceScore;

    @Column(name = "education_score")
    private Double educationScore;

    @Column(name = "keywords_match")
    private Double keywordsMatch;

    @Column(name = "overall_fit")
    private Double overallFit;

    @Column(name = "growth_potential")
    private Double growthPotential;

    @Column(name = "recommendation_decision", length = 20)
    private String recommendationDecision;

    @Column(name = "recommendation_reason", columnDefinition = "TEXT")
    private String recommendationReason;

    @Column(name = "recommendation_confidence", length = 20)
    private String recommendationConfidence;

    @Column(name = "skill_match_percentage")
    private Double skillMatchPercentage;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "tb_matching_skills", columnDefinition = "jsonb")
    private List<String> matchingSkills;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "tb_missing_skills", columnDefinition = "jsonb")
    private List<String> missingSkills;

    @Column(name = "experience_level", length = 20)
    private String experienceLevel;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "tb_matching_experience", columnDefinition = "jsonb")
    private List<String> matchingExperience;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "tb_experience_gaps", columnDefinition = "jsonb")
    private List<String> experienceGaps;

    @Column(name = "education_level", length = 20)
    private String educationLevel;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "tb_education_highlights", columnDefinition = "jsonb")
    private List<String> educationHighlights;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "tb_matching_education", columnDefinition = "jsonb")
    private List<String> matchingEducation;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "tb_missing_education", columnDefinition = "jsonb")
    private List<String> missingEducation;

    @Column(name = "is_fresher")
    private Boolean isFresher = true;

    @Column(name = "first_job_start_year")
    private Integer firstJobStartYear;

    @Column(name = "last_job_end_year")
    private Integer lastJobEndYear;

    @Column(name = "total_jobs_count")
    private Integer totalJobsCount = 0;

    @Column(name = "average_job_change", length = 50)
    private String averageJobChange;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "tb_strengths", columnDefinition = "jsonb")
    private List<String> strengths;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "tb_weaknesses", columnDefinition = "jsonb")
    private List<String> weaknesses;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "tb_red_flags", columnDefinition = "jsonb")
    private List<String> redFlags;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "tb_cultural_fit_indicators", columnDefinition = "jsonb")
    private List<String> culturalFitIndicators;

    @Column(name = "salary_expectation_alignment", length = 20)
    private String salaryExpectationAlignment;

    @Column(name = "onboarding_priority", length = 20)
    private String onboardingPriority;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "tb_interview_focus_areas", columnDefinition = "jsonb")
    private List<String> interviewFocusAreas;

    @Column(name = "processing_time")
    private Double processingTime;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(name = "file_path", columnDefinition = "TEXT")
    private String filePath;

    @Column(name = "file_size")
    private Double fileSize;

    @Column(name = "word_count")
    private Integer wordCount;

    @Column(name = "success")
    private Boolean success = true;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "status", length = 20)
    private String status = "Not Shortlisted";

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @Column(name = "job_id")
    private Integer jobId;

    @Column(name = "email_sent")
    private Boolean emailSent = false;
}