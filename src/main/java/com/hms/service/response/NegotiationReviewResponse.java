package com.hms.service.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NegotiationReviewResponse {

	private String stage;

	private String status; 

	private String approvedBy;

	private String role;

	private LocalDateTime approvedOn;

}
