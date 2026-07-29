package com.hms.service.request;

import java.time.LocalDate;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NegotiationFieldRequest {

	     private List<Negotiation> negotiation;

	    private String others;

	    private String overallJustification;

	    private List<MultipartFile> documents;
	    
	    private Integer offerId;
	    
	    private Integer jobId;
	    
	    private LocalDate joiningDate;
	    
	    private Integer applicantId;
	    
	    private String reasonForJoiningDate;
}
