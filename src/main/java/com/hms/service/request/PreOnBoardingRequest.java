package com.hms.service.request;


import java.util.List;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class PreOnBoardingRequest {
	private String firstName;
	private String lastName;
	private String middleName;
	private String gender;
	private String dateOfBirth;
	private String nationality;
	private String aadharNumber;

	private String city;
	private String address1;
	private String state;
	private String pincode;
	private String country;

	private String addressInfo;
	private String personalInfo;

	private String highestEducationQualification;
	private String cgpa;
	private Integer year;
	private Boolean isFresher;

	private String educationDocument;
	private String aadharPhoto;

	private String email;
	private String phoneNumber;
	private List<String> organizationNames;

	private String remarks;
	private String experience;

	private Integer candidateId;

}
