package com.hms.service.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CandidateUpdateResponse {

	private Integer id;
	private String firstName;
	private String lastName;
	private String phoneNumber;
	private String email;
	private String jobTitle;
	private String jobCountry;
	private String department;
	private String status;
	private Double ctc;
	private String offerLetter;
	private String githubURL;
	private String linkedinURL;

}
