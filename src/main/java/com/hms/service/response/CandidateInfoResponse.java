package com.hms.service.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@AllArgsConstructor
@NoArgsConstructor

public class CandidateInfoResponse {

	private Integer id;
	private Integer candidateId;
	private String firstName;
	private String lastName;
	private String phoneNumber;
	private String email;
	private String jobTitle;
	private String jobCountry;
	private LocalDateTime createdDate;
	private LocalDateTime updatedDate;
	private String department;
	private String status;
	private Double ctc;
	private String offerLetter;
	private String description;
	private String vendorStatus;
	private String reportUrl;
	private String finalStatus;
	private String responseMessage;
	private String responseCode;
	private Integer applicationId;
	List<Map<String,String>> offers;
	private LocalDateTime issuedDate;

	private String githubURL;
	private String linkedinURL;

}
