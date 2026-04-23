package com.hms.service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Table(name = "tb_sourcing_entity")
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class SourcingStrategyEntity {
	 
	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    @Column(name="id",updatable=false,nullable=false)
	    private Integer id;
	 
	    @Column(name = "sr_id", unique = true, nullable = false)
	    private String srId;
	 
	    @Column(name = "internal_board")
	    private Boolean internalBoard = false;
	 
	    @Column(name = "naukri")
	    private Boolean naukri = false;
	 
	    @Column(name = "linkedin")
	    private Boolean linkedIn = false;
	 
	    @Column(name = "indeed")
	    private Boolean indeed = false;
	 
	    @Column(name = "company_site")
	    private Boolean companySite = false;
	 
	    @Column(name = "agency_rpo")
	    private Boolean agencyRpo = false;
	 
	    @Column(name = "internal_first_policy")
	    private Boolean internalFirstPolicy = true;
	 
	    @Column(name = "sourcing_budget")
	    private Integer sourcingBudget;
	 
	    @Column(name = "referral_enabled")
	    private Boolean referralEnabled = false;
	 
	    @Column(name = "referral_amount")
	    private Double referralAmount;
	 
	    @Column(name = "diversity_enabled")
	    private Boolean diversityEnabled = false;
	 
	    @Column(name = "diversity_tags", columnDefinition = "TEXT")
	    private String diversityTags;
		
		@Column(name="submitted")
		private Boolean submitted;
		
		@Column(name="approved")
		private Boolean approved;
	}


