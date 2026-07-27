package com.hms.service.entity;

import java.util.List;

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
@Table(name = "tb_negotiation_documents")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NegotiationDocumentsEntity {
	
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
	
	@Column(name = "supporting_documents", columnDefinition = "jsonb")
	private List<String> supportingDocuments;
	
	@Column(name="overall_justification")
	private String overallJustification;
	
	@ManyToOne
	@JoinColumn(name = "negotiation_id", referencedColumnName = "id")
	private NegotiationOfferEntity negotiation;

}
