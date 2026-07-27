package com.hms.service.request;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NegotiateOfferRequest {
	
	private long requestedAmount;
	
	private String justification;
	
	private String field;
	
	private String others;
	
	private String overallJustification;
	
	private List<MultipartFile> documents;
	
	private Long previousAmount;
	
	
	

}
