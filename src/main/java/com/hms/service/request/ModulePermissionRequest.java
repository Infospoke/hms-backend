package com.hms.service.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class ModulePermissionRequest {
	
	private Integer moduleId;
	
	private Boolean create;

	private Boolean view;

	private Boolean edit;

	private Boolean delete;

}
