package com.hms.service.response;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor

public class MyApplicationResponse {
	
	    private Integer applicationId;
	    private Integer jobId;
	    private String jobTitle;
	    private String location;
	    private String employmentType;
	    private LocalDateTime appliedDate;
	    private Long daysAfterApplied;
	    private Integer totalRounds;
	    private Integer completedRounds;
	    private String currentRound;
	    private String reuploadStatus;
	    private List<ApplicationTimeLineResponse> timeline;

	}


