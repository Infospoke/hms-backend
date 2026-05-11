package com.hms.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApprovalsLevelsDto {
	
    private Integer level;
    private Integer roleId;
    private Integer departmentId;


}
