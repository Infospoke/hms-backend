package com.hms.service.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateInterviewFeedbackRequest {
	
	private int id;
	private String decision;
	private int jobId;
	private int StageTypeId;


}
