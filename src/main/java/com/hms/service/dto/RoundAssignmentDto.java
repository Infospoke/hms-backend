package com.hms.service.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoundAssignmentDto {

	    private String stageName;

	    private String stageType;

	    private Long interviewerUserId;

	    private String interviewerName;

	    private String status;
	    
	    private String comments;

	    private LocalDateTime respondedAt;
	    
	    private String roleName;
	    
	    private Integer stageTypeId;
	    
}
