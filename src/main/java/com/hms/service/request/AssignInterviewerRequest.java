package com.hms.service.request;

import java.util.List;

import com.hms.service.dto.RoundAssignmentDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssignInterviewerRequest {

	private Integer jobId;

	private Integer planId;

	private List<RoundAssignmentDto> assignments;
}
