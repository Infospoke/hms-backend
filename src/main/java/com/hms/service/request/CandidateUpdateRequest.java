package com.hms.service.request;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CandidateUpdateRequest {

	private int id;
	private String firstName;
	private String lastName;
	private String phoneNumber;
	private String email;
	private String jobTitle;
	private String department;
	private String status;
	private String jobCountry;
	private String vendorStatus;
	private String finalStatus;
	private Integer ctc;
	private String description;
	private String githubURL;
	private String linkedinURL;

	

}