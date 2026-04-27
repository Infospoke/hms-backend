package com.hms.service.entity;

import java.time.LocalDateTime;

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
@Table(name = "tb_offer")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OfferEntity {

 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 @Column(name = "id", nullable = false)
 private int id;

 @ManyToOne
 @JoinColumn(name = "candidate_id", referencedColumnName = "id")
 private CandidateInfoEntity candidateId;

 @Column(name = "offer_letter_path")
 private String offerLetterPath;

 @Column(name = "ctc")
 private Integer ctc;

 @Column(name = "issue_date")
 private LocalDateTime issueDate;

 @Column(name = "accepted_date")
 private LocalDateTime acceptedDate;

 @Column(name = "status")
 private String status;

}
 