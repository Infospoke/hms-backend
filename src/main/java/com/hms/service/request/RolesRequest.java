package com.hms.service.request;

import com.hms.service.constants.Constants;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RolesRequest {

	private Integer roleId;

	@NotBlank(message = Constants.ROLE_NAME_IS_REQUIRED)
	private String roleName;

	@NotNull(message = Constants.BUSINESS_UNIT_REQUIRED)
	private Integer businessUnitId;

	@NotNull(message = Constants.DEPARTMENT_REQUIRED)
	private Integer departmentId;

	private String description;

	private PermissionRequest permission;

}
