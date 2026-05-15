package com.hms.service.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApprovalChainRequest {
	
	private Integer id;

    @NotBlank(message = "Chain Name is required")
    @Size(min = 3, max = 100, message = "Chain Name must be between 3 and 100 characters")
    private String chainName;

    @Size(max = 300, message = "Description must not exceed 300 characters")
    private String description;

    @NotBlank(message = "Status is required")
    private String status;
    
    @NotNull(message = "Functionality is required")
    private Integer functionality;

    private String approval;
 
    
    @Valid  
    @NotEmpty(message = "Level configuration is required")
    @Size(max = 3, message = "Maximum 3 levels allowed")
    private List<LevelConfig> levelConfig;
}