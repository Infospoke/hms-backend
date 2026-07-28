package com.hms.service.request;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NegotiationFieldRequest {

	     private List<NegotiateOfferRequest> fields;

	    private String others;

	    private String overallJustification;

	    private List<MultipartFile> documents;
}
