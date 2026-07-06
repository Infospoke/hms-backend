package com.hms.service.request;

import java.util.List;

import com.hms.service.dto.RecruiterInfoDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecuriterAssignmentRequest {
	private List<RecruiterInfoDto> recruiterInfoDtos;
	
	private String srId;
	
	private Integer jobId;

  
}
