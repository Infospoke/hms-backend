package com.hms.service.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {

    
	private Integer id;
    private String username;
    private String email;
    private Integer roleId;
    private String roleName;
    private Boolean active;
   
}
