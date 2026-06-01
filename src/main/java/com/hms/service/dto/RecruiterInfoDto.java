package com.hms.service.dto;

import lombok.Data;

@Data
public class RecruiterInfoDto {
	private Integer userId;
	private String email;
	private String userName;
	private Integer roleId;
	private String roleName;
}
