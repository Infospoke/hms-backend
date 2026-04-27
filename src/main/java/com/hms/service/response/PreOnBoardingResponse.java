package com.hms.service.response;
 
import java.util.List;
import java.util.Map;
 

 
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
 
 
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PreOnBoardingResponse {
 
	private Integer id;
	private Integer candidateId;
	private String firstName;
	private String lastName;
	private String middleName;
	private String gender;
	private String personalInfo;
	private String addressInfo;
	private String dateOfBirth;
	private String nationality;
	private String aadharNumber;
	private String city;
	private String address1;
	private String state;
	private String pincode;
	private String country;
	private String highestEducationQualification;
	private String cgpa;
	private Integer year;
	private Boolean isFresher;
	private String educationDocument;
	private String aadharPhoto;
	private String remarks;
	private String experience;
	private String phoneNumber;
	private String email;
    private String paySlips;
	private List<Map<String, String>> organizationExperience;
}
 
 