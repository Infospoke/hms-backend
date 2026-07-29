package com.hms.service.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CandidateDetailsResponse {

	private String candidateId;

	private String firstName;

	private String lastName;

	private String email;

	private String phoneNumber;

	private String resume;

	private String additionalFile;


}