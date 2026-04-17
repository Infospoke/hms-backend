package com.hms.service.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor

public class RolesInfoResponse {
	
	private Integer roleId;
	private String roleName;
	private LocalDateTime createdDate;
	private String description;
	private String BusinessUnit;
	private String department;

}
