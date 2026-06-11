package com.hms.service.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateInterviewAssignmentRequest {

	    private Integer Id;

	    private String status;

	    private String comments;
	
}
