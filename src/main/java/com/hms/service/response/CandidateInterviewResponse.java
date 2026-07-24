package com.hms.service.response;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class CandidateInterviewResponse {


	    private Integer applicationId;

	    private Integer currentStageId;

	    private String interviewType;

	    private String jobTitle;

	    private LocalDate interviewDate;

	    private LocalTime startTime;

	    private LocalTime endTime;

	    private String duration;

	    private String recruiterName;
	
}
