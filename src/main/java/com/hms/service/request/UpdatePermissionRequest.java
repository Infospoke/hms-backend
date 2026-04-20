package com.hms.service.request;

import com.hms.service.constants.Constants;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePermissionRequest {
	
	 
	@NotNull(message = Constants.ROLE_REQUIRED)
	Integer roleId;
	
	PermissionRequest permission;
}
