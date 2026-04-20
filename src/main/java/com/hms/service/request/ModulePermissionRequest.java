package com.hms.service.request;


import com.hms.service.constants.Constants;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class ModulePermissionRequest {
	
	@NotNull(message = Constants.MODULE_ID_REQUIRED)
	private Integer moduleId;
	
	private Boolean create;

	private Boolean view;

	private Boolean edit;

	private Boolean delete;

}
