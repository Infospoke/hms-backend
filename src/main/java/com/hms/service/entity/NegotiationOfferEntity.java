package com.hms.service.entity;


import java.time.LocalDate;


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
	
	
	@Column(name="filed_name")
	private String fieldName;
	
	@Column(name="requested_amount")
	private Long requestedAmount;
	
	@Column(name="offered_amount")
	private Long offeredAmount;
	
	@Column(name="justification")
	private String justification;
	
	@Column(name="approved_amount")
	private Long approvedAmount;
	
	@Column(name="approvalStatus")
	private String approvalStatus;
	
	@ManyToOne
	@JoinColumn(name = "job_id", referencedColumnName = "job_id")
	private CreateJobDetailsEntity job;
	
	@Column(name="offer_negotiated_date")
	private LocalDate offerNegotiatedDate;
	 

	
	
	

}
