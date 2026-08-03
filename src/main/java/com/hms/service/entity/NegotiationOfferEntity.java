package com.hms.service.entity;


import java.time.LocalDate;
import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.hms.service.request.Negotiation;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "tb_negotiation_offer")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NegotiationOfferEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false, updatable = false)
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name = "candidate_id", referencedColumnName = "candidate_id")
	private CandidateCreationDetailsEntity candidate;
	
	@ManyToOne
	@JoinColumn(name = "offer_id", referencedColumnName = "id")
	private OfferDetailsEntity offer;
	
	@Column(name="approved_amount")
	private Long approvedAmount;
	
	@Column(name="approval_status")
	private String approvalStatus;
	
	@ManyToOne
	@JoinColumn(name = "job_id", referencedColumnName = "job_id")
	private CreateJobDetailsEntity job;
	
	@Column(name="offer_negotiated_date")
	private LocalDate offerNegotiatedDate;
	
	@Column(name = "supporting_documents", columnDefinition = "jsonb")
	private List<String> supportingDocuments;
	
	@Column(name="overall_justification")
	private String overallJustification;
	
	@Column(name="others")
	private String others;
	
    @JdbcTypeCode(SqlTypes.JSON)
   	@Column(name = "negotiation", columnDefinition = "jsonb")
   	private List<Negotiation> negotiation;
    
    @Column(name="joining_date")
    private LocalDate joiningDate;
    
    @Column(name="total_requested_amount")
    private Long totalRequestedAmount;
    
    @ManyToOne
	@JoinColumn(name = "applicant_id", referencedColumnName = "id")
	private JobApplicationEntity applicant;
    
    @Column(name="joining_date_reason")
    private String joiningDateReason;
	
   
}