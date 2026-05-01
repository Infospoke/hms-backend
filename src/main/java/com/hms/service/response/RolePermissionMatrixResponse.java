package com.hms.service.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data

@AllArgsConstructor

@NoArgsConstructor

public class RolePermissionMatrixResponse {

	private Integer roleId;
	private String roleName;
	private String description;
	private Long userCount;
	

}
