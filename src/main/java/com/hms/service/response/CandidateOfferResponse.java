package com.hms.service.response;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CandidateOfferResponse {
	
	private String jobTitle;
	
	private String jobLocation;
	
	private Long totalCtc;
	
	private String employmentType;
	
	private Integer offerId;
	
	private LocalDate dueDate;
	
	private String status;
	 
	

}
