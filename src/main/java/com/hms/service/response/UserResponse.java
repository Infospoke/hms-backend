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
    private Integer roleId;
    private String email;
    private String roleName;
    private boolean status;
}
