package com.hms.service.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@Table(name="tb_resume_analysis")

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResumeAnalysisEntity {
	
	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Integer id;

	    @Column(name = "application_id", unique = true, nullable = false)
	    private Integer applicationId;
	    
	    @Column(name="jobId")
	    private Integer jobId;

	    @Column(name = "candidate_name", length = 150)
	    private String candidateName;

	    @Column(length = 255)
	    private String email;
	    
	    @Column(name="status")
	    private String status;

	    @Column(name = "contact_number", length = 50)
	    private String contactNumber;

	    private Double finalScore;
	    private Double skillsMatch;
	    private Double experienceScore;
	    private Double educationScore;
	    private Double keywordsMatch;
	    private Double overallFit;
	    private Double growthPotential;

	    @Column(name = "recommendation_decision", length = 20)
	    private String recommendationDecision;

	    @Column(name = "recommendation_reason", columnDefinition = "TEXT")
	    private String recommendationReason;

	    @Column(name = "recommendation_confidence", length = 20)
	    private String recommendationConfidence;

	    private Double skillMatchPercentage;

	    // JSON-like fields (stored as separate tables)
	    @ElementCollection
	    @CollectionTable(name = "tb_matching_skills", joinColumns = @JoinColumn(name = "resume_analysis_id"))
	    @Column(name = "skill")
	    private List<String> matchingSkills;

	    @ElementCollection
	    @CollectionTable(name = "tb_missing_skills", joinColumns = @JoinColumn(name = "resume_analysis_id"))
	    @Column(name = "skill")
	    private List<String> missingSkills;

	    @Column(name = "experience_level", length = 20)
	    private String experienceLevel;

	    @ElementCollection
	    @CollectionTable(name = "tb_matching_experience", joinColumns = @JoinColumn(name = "resume_analysis_id"))
	    @Column(name = "experience")
	    private List<String> matchingExperience;

	    @ElementCollection
	    @CollectionTable(name = "tb_experience_gaps", joinColumns = @JoinColumn(name = "resume_analysis_id"))
	    @Column(name = "gap")
	    private List<String> experienceGaps;

	    @Column(name = "tb_education_level", length = 20)
	    private String educationLevel;

	    @ElementCollection
	    @CollectionTable(name = "tb_education_highlights", joinColumns = @JoinColumn(name = "resume_analysis_id"))
	    @Column(name = "highlight")
	    private List<String> educationHighlights;

	    @ElementCollection
	    @CollectionTable(name = "tb_matching_education", joinColumns = @JoinColumn(name = "resume_analysis_id"))
	    @Column(name = "education")
	    private List<String> matchingEducation;

	    @ElementCollection
	    @CollectionTable(name = "tb_missing_education", joinColumns = @JoinColumn(name = "resume_analysis_id"))
	    @Column(name = "education")
	    private List<String> missingEducation;

	    private Boolean isFresher = true;

	    private Integer firstJobStartYear;
	    private Integer lastJobEndYear;

	    private Integer totalJobsCount = 0;

	    @Column(name = "average_job_change", length = 50)
	    private String averageJobChange;

	    @ElementCollection
	    @CollectionTable(name = "tb_strengths", joinColumns = @JoinColumn(name = "resume_analysis_id"))
	    @Column(name = "value")
	    private List<String> strengths;

	    @ElementCollection
	    @CollectionTable(name = "tb_weaknesses", joinColumns = @JoinColumn(name = "resume_analysis_id"))
	    @Column(name = "value")
	    private List<String> weaknesses;

	    @ElementCollection
	    @CollectionTable(name = "tb_red_flags", joinColumns = @JoinColumn(name = "resume_analysis_id"))
	    @Column(name = "value")
	    private List<String> redFlags;

	    @ElementCollection
	    @CollectionTable(name = "tb_cultural_fit_indicators", joinColumns = @JoinColumn(name = "resume_analysis_id"))
	    @Column(name = "value")
	    private List<String> culturalFitIndicators;

	    @Column(name = "salary_expectation_alignment", length = 20)
	    private String salaryExpectationAlignment;

	    @Column(name = "onboarding_priority", length = 20)
	    private String onboardingPriority;

	    @ElementCollection
	    @CollectionTable(name = "tb_interview_focus_areas", joinColumns = @JoinColumn(name = "resume_analysis_id"))
	    @Column(name = "value")
	    private List<String> interviewFocusAreas;

	    private Double processingTime;

	    private LocalDateTime processedAt;

	    @Column(name = "file_path", columnDefinition = "TEXT")
	    private String filePath;

	    private Double fileSize;
	    private Integer wordCount;

	    private Boolean success = true;

	    @Column(name = "error_message", columnDefinition = "TEXT")
	    private String errorMessage;

	    private LocalDateTime createdAt;
	    private LocalDateTime updatedAt;

	    private Boolean isDeleted = false;
}
