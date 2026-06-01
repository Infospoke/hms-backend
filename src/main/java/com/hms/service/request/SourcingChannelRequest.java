package com.hms.service.request;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SourcingChannelRequest {
	 
	    private Map<String , Boolean> channels;
	    private Boolean referral;
	    private double referralAmount;
	}
