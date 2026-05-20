package com.hms.service.request;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobDescriptionRequest {

	private Integer jobId;
	
	private String description;
	
}
