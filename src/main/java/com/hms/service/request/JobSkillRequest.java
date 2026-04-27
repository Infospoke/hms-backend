package com.hms.service.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobSkillRequest {
	
    private Integer skillId;
    private String skillName;
    private Integer categoryId;
    private Integer experienceLevel;
    private Integer weightage;

}
