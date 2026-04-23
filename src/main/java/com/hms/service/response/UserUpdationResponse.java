package com.hms.service.response;

import java.time.LocalDate;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdationResponse {
	
	

	    private String username;
	    private String email;
	    private Boolean active;

	    private String roleName;
	    private String assignedBy;
	    private LocalDate assignedAt;

}
