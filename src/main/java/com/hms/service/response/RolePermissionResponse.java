package com.hms.service.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class RolePermissionResponse {
	

	    private Integer roleId;
	    private String roleName;
	    private Integer totalModules;
	    private List<ModulePermissionResponse> modules;
	}


