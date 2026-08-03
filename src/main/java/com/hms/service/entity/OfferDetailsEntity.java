package com.hms.service.entity;

import java.time.LocalDate;
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
    
    @Column(name="joining_date")
    private LocalDate joiningDate;
  
    @Column(name = "created_by_roleId")
    private Integer createdByRoleId;

    @Column(name = "submit_financial_approval")
    private Boolean submitFinancialApproval=false;

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
    
    @Column(name="date_of_approval1")
	private LocalDateTime dateOfApproval1;
	
    @Column(name="approver1_comments")
    private String approver1Comments;

    
    @Column(name="approver2")
    private Boolean approver2=false;
    
    @Column(name="approver2_by")
    private String approver2By;
    
    @Column(name="approver2_role")
    private String approver2Role;
    
    @Column(name="approver2_comments")
    private String approver2Comments;
      
    @Column(name="date_of_approval2")
	private LocalDateTime dateOfApproval2;

    
    @Column(name="approver3")
    private Boolean approver3=false;
    
    @Column(name="approver3_by")
    private String approver3By;
    
    @Column(name="approver3_role")
    private String approver3Role;
    
    @Column(name="approver3_comments")
    private String approver3Comments;
   
    @Column(name="date_of_approval3")
	private LocalDateTime dateOfApproval3;

    
    @Column(name="reject")
    private Boolean reject=false;
    
    @Column(name="approve")
    private Boolean approve=false;
    
    @Column(name="final_approval_time")
    private LocalDateTime finalApprovalTime;

    @Column(name="offer_released")
    private Boolean offerReleased=false;
    
    @Column(name="in_progress")
    private Boolean inProgress;
    
    @Column(name="submitted_by_user_id")
    private Integer submittedByUserId;
    
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
    
    @Column(name="total_ctc")
    private Long totalCtc;

    @Column(name = "compensation")
    private String compensation;
    
    @ManyToOne
    @JoinColumn(name = "offer_letter_template_id")
    private OfferLetterTemplateEntity offerLetterTemplate;
    
    @Column(name="offer_status")
    private String offerStatus;

    
}
