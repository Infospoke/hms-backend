package com.hms.service.response;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StaffingRequisitionResponse {

	    private String srId;
	    private String jobTitle;
	    private Integer businessUnitId;
	    private Integer departmentId;
	    private List<Integer> reportingManagerInfo;
	    private String location;
	    private String seniorityLevel;
	    private Integer openings;
	    private LocalDate targetStartDate;
	    private String workMode;
	    private String employmentType;
	    private String priority;
	    private String requisitionType;
	    private String businessCase;
	    private String impactIfNotFilled;
	    private Integer replacesEmployee;
	    private String document;
	    private Integer proposedTotalCompensation;
	    private Boolean signingBonus;
	    private Boolean equity;
	    private Boolean relocationBudget;
	    private Integer signingBonusAmount;
	    private Integer equityAmount;
	    private Integer relocationBudgetAmount;
	    private Integer annualHiringCost;
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
	    private Boolean internalBoard;
	    private Boolean naukri;
	    private Boolean linkedIn;
	    private Boolean indeed;
	    private Boolean companySite;
	    private Boolean agencyRpo;
	    private Boolean internalFirstPolicy;
	    private Integer sourcingBudget;
	    private Boolean referralEnabled;
	    private Double referralAmount;
	    private Boolean diversityEnabled;
	    private String diversityTags;
	    private Boolean draft;
	    private Boolean submitted;
	    private Boolean approved;

}
