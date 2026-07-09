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
}
