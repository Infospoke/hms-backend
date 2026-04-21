package com.hms.service.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BusinessJustificationRequest {
	
		private String requisitionType;
	 
		private String businessCase;
	 
		private String impactIfNotFilled;
	 
		private Integer replacesEmployee;
	 
		private String document;

}
