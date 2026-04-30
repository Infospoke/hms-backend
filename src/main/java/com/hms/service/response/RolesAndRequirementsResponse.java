package com.hms.service.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RolesAndRequirementsResponse {

    private Integer id;
    private String srId;

    private List<String> skillsMustHave;
    private List<String> niceToHaveSkills;

    private String educationRequirement;
    private String travelRequirement;

    private Integer minExperience;
    private Integer maxExperience;

    private Integer minInterviewRounds;
    private Integer maxInterviewRounds;

    private List<String> certificationsRequired;
    private List<String> languages;

    private Boolean assessmentRequired;

    
    private Boolean submitted;
    private Boolean approved;
}
