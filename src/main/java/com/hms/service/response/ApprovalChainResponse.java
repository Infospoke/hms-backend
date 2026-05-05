package com.hms.service.response;

import java.time.LocalDate;
import java.util.List;

import com.hms.service.request.LevelConfig;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApprovalChainResponse {

	private Long id;
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
}