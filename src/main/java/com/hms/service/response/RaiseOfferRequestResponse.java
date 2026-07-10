package com.hms.service.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RaiseOfferRequestResponse {

	    private Integer offerId;

	    private Integer applicantId;

	    private String candidateName;

	    private String candidateEmail;

	    private String phoneNumber;

	    private Integer jobId;

	    private String jobTitle;

	    private String departmentName;

	    private LocalDateTime movedToHireOn;

	    private String recruiter;

	    private String priority;
	

}
