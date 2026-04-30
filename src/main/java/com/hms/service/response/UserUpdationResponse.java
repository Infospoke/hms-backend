package com.hms.service.response;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdationResponse {

	private Integer userTypeId;
	private String firstName;
	private String lastName;
	private Integer employeeId;
	private String email;
	private String mobileNumber;
	private String alternateContact;
	private Integer employmentTypeId;
	private Integer businessUnitId;
	private Integer departmentId;
	private Integer roleId;
	private String roleName;
	private String assignedBy;
	private LocalDate assignedAt;
	private Boolean active;
}