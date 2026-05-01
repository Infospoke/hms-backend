package com.hms.service.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RolePermissionResponse {
	

	    private Integer roleId;
	    private String roleName;
	    private Integer moduleId;
	    private String moduleName;
	    private List<ModulePermissionResponse> subModules;
	}


