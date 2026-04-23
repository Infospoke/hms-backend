package com.hms.service.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RolesAndRequirementsResponse {

    private Integer id;
    private String srId;

    private String skillsMustHave;
    private String niceToHaveSkills;

    private String educationRequirement;
    private String travelRequirement;

    private Integer minExperience;
    private Integer maxExperience;

    private Integer minInterviewRounds;
    private Integer maxInterviewRounds;

    private String certificationsRequired;
    private String languages;

    private Boolean assessmentRequired;

    private Boolean draft;
    private Boolean submitted;
    private Boolean approved;
}
