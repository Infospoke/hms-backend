package com.hms.service.response;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hms.service.request.LevelConfig;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApprovalChainResponse {

	

	private Integer id;
    private String chainName;
    private String description;
    private String status;
    private Integer levels;

    private String updatedBy;
    private LocalDate updatedAt;

    private LocalDate createdAt;
    private String createdBy;

    private String approval;

    private List<LevelConfig> levelConfig;
    
    private Integer functionality;
    
    private String functionalityName;
    
    private String activateComments;
    
    private String deactivateComments;
    
    private String approvedComments;
    
    private String rejectedComments;
    
    private String requestType;

    private List<CommentTimelineResponse> commentTimeline;

}