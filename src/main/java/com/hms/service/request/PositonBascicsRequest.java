package com.hms.service.request;

import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PositonBascicsRequest {
	private Integer id;
	
	private String jobTitle;

	private Integer departmentId;

	private Integer businessUnitId;

	private List<Integer> reportingManagerInfo;

	private String location;

	private String seniorityLevel;

	private Integer openings;

	private LocalDate targetStartDate;

	private String workMode;

	private String employmentType;

	private String priority;


}
