package com.hms.service.entity;
  
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
@Table(name = "tb_BGV")
@Data
@AllArgsConstructor
@NoArgsConstructor
 
public class BGVEntity {
 
 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 @Column(name = "id", nullable = false)
 private Integer id;
 
 @Column(name="vendor_status")
 private String vendorStatus;
 
 @Column(name="final_status")
 private String finalStatus;
 
 @Column(name="report_url")
 private String reportUrl;
 
 @ManyToOne
 @JoinColumn(name="candidate_id",referencedColumnName="id")
 private CandidateInfoEntity candidateId;
}  