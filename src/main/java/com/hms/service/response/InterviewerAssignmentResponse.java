package com.hms.service.response;

import java.util.List;

import com.hms.service.dto.RoundAssignmentDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InterviewerAssignmentResponse {

	private Integer jobId;

	private String jobTitle;

	private String deptName;

	private String planName;

	private List<RoundAssignmentDto> rounds;
	
}
