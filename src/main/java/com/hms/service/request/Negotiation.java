package com.hms.service.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Negotiation {
	
	private String fieldName;
	
	private Long initialAmount;
	
	private Long requestedAmount;
	
	private String reason;

}
