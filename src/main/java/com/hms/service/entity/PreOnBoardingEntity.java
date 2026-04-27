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
@Table(name = "tb_pre_onboarding")
@Data
@AllArgsConstructor
@NoArgsConstructor
 
public class PreOnBoardingEntity {
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false, updatable = false)
	private Integer id;
 
	@Column(name = "first_name")
	private String firstName;
 
	@Column(name = "last_name")
	private String lastName;
 
	@Column(name = "middle_name")
	private String middleName;
 
	@Column(name = "gender")
	private String gender;
 
	@Column(name = "personal_info")
	private String personalInfo;
 
	@Column(name = "address_info")
	private String addressInfo;
	
	@Column(name = "date_of_birth")
	private String dateOfBirth;
 
	@Column(name = "nationality")
	private String nationality;
 
	@Column(name = "aadhar_number")
	private String aadharNumber;
 
	@Column(name = "city")
	private String city;
 
	@Column(name = "address1")
	private String address1;
 
	@Column(name = "state")
	private String state;
 
	@Column(name = "pincode")
	private String pincode;
 
	@Column(name = "country")
	private String country;
  
	@Column(name = "highest_education_qualification")
	private String highestEducationQualification;
 
	@Column(name = "cgpa")
	private String cgpa;
 
	@Column(name = "year")
	private Integer year;
 
	@Column(name = "is_fresher")
	private Boolean isFresher;
 
	@Column(name = "education_document")
	private String educationDocument;
 
	@Column(name = "bank_photo")
	private String bankPhoto;
 
	@Column(name = "aadhar_photo")
	private String aadharPhoto;
 
	@Column(name = "remarks")
	private String remarks;
 
	@Column(name = "experience")
	private String experience;
 
	@Column(name = "phone_number")
	private String phoneNumber;
 
	@Column(name = "email")
	private String email;
    
	@Column(name = "payslips")
    private String paySlips;
	
	@Column(name="created_date")
	private LocalDateTime createdDate;
    
 
 
	@Column(name = "organization_details", columnDefinition = "Text")
	private String organizationDetails;
 
  
	@ManyToOne
	@JoinColumn(name = "candidate_id", referencedColumnName = "id")
	private CandidateInfoEntity candidateId;
	
}
 