package com.hms.service.request;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NegotiateOfferRequest {
	

    private String fields;

    private Long previousAmount;

    private Long requestedAmount;

    private String justification;
	
	
	

}
