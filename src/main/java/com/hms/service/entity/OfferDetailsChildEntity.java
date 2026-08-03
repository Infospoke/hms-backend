package com.hms.service.entity;

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
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name="tb_offer_details_child_entity")
public class OfferDetailsChildEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false, updatable = false)
	private Integer id;
	
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", referencedColumnName = "id")
    private JobApplicationEntity jobApplication;
	
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offer_id", referencedColumnName = "id")
    private OfferDetailsEntity offer;
    
    
    @Column(name="approver1")
    private Boolean approver1=false;
    
    @Column(name="approver2")
    private Boolean approver2=false;
    
    @Column(name="approver3")
    private Boolean approver3=false;
    
    @Column(name="role1")
    private Integer role1;
    
    @Column(name="role2")
    private Integer role2;
    
    @Column(name="role3")
    private Integer role3;
    
    @Column(name="offer_submitted_by")
    private Integer offerSubmittedBy;
    
    @Column(name="negotiation")
    private Boolean negotiation=false;
    
    
	

}
