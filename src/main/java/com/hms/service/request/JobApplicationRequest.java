package com.hms.service.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobApplicationRequest {
	

	@NotBlank(message="First Name is required")
    private String firstName;
	
	@NotBlank(message="Last Name is required")
    private String lastName;
	
	@Email(message="Email is required")
    private String email;
	
	@NotBlank(message="Phone Number is required")
    private String phNo;
	
    private String location;
    
    private Integer jobId;
  
    private Boolean referral;
 
    private String jobCountry;
 

}
