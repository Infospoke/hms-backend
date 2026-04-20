package com.hms.service.response;

import com.hms.service.enums.UserStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {

    
	private Integer id;
    private String name;
    private Integer roleId;
    private String email;
    private String roleName;
    private UserStatus status;
}