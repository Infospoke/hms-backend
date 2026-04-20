package com.hms.service.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModulePermissionResponse {

	private Integer moduleId;
	private String moduleName;

	private Boolean create;
	private Boolean view;
	private Boolean edit;
	private Boolean delete;
}
