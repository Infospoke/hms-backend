package com.hms.service.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_offer_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OfferDetailsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_application_id", nullable = false)
    private JobApplicationEntity jobApplication;

    @Column(name = "notice_period")
    private String noticePeriod;

    @Column(name = "probation_period")
    private String probationPeriod;

    @Column(name = "submit_financial_approval")
    private Boolean submitFinancialApproval;

    @Column(name = "created_date")
    private LocalDateTime createdDate;
    
    @Column(name = "responded_at")
    private LocalDateTime respondedAt;
    
    @Column(name="approver1")
    private Boolean approver1=false;
    
    @Column(name="approver1_by")
    private String approver1By;
    
    @Column(name="approver1_role")
    private String approver1Role;
    
    @Column(name="approver1_comments")
    private Boolean approver1_comments;
    
    @Column(name="approver2")
    private Boolean approver2=false;
    
    @Column(name="approver2_by")
    private String approver2By;
    
    @Column(name="approver2_role")
    private String approver2Role;
    
    @Column(name="approver2_comments")
    private Boolean approver2_comments;
    
    @Column(name="approver3")
    private Boolean approver3=false;
    
    @Column(name="approver3_by")
    private String approver3By;
    
    @Column(name="approver3_role")
    private String approver3Role;
    
    @Column(name="approver3_comments")
    private Boolean approver3_comments;
    
    @Column(name="reject")
    private Boolean reject=false;
    
    
    @Column(name="approve")
    private Boolean approve=false;
    
    @Column(name="final_approval_time")
    private LocalDateTime finalApprovalTime;

    @Column(name="offer_released")
    private Boolean offerReleased=false;
    
    @Column(name="offer_released_by")
    private Long offerReleasedBy;
    
    @Column(name="offer_released_at")
    private LocalDateTime offerReleasedAt;

    @Column(name="interview_completion_status")
	private String interviewCompletionStatus;
    
    @Column(name = "interview_completion_date")
    private LocalDateTime interviewCompletionDate;

    @Column(name = "recruited_by")
    private String recruitedBy;

    
}
