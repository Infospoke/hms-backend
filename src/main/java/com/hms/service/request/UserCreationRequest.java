package com.hms.service.request;

import com.hms.service.constants.Constants;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCreationRequest {

    @NotNull(message = Constants.USER_TYPE_REQUIRED)
    private Integer userTypeId;

    @NotBlank(message = Constants.FIRST_NAME_REQUIRED)
    @Size(min = 2, max = 50, message = Constants.FIRST_NAME_SIZE)
    @Pattern(regexp = "^[A-Za-z]+$", message = Constants.FIRST_NAME_INVALID)
    private String firstName;

    @NotBlank(message = Constants.LAST_NAME_REQUIRED)
    @Size(min = 1, max = 50, message = Constants.LAST_NAME_SIZE)
    @Pattern(regexp = "^[A-Za-z]+$", message = Constants.LAST_NAME_INVALID)
    private String lastName;

    @NotNull(message = Constants.EMPLOYEE_ID_REQUIRED)
    private Integer employeeId;

    @NotBlank(message = Constants.EMAIL_REQUIRED)
    @Email(message = Constants.EMAIL_INVALID)
    private String email;

    @NotBlank(message = Constants.MOBILE_REQUIRED)
    @Pattern(regexp = "^\\+?[0-9]+$", message = Constants.MOBILE_INVALID)
    private String mobileNumber;

    @Pattern(regexp = "^\\+?[0-9]*$", message = Constants.ALT_MOBILE_INVALID)
    private String alternateContact;

    @NotNull(message = Constants.DOB_REQUIRED)
    private String dateOfBirth; 

    @NotNull(message = Constants.EMPLOYMENT_TYPE_REQUIRED)
    private Integer employmentTypeId;

    @NotNull(message = Constants.BUSINESS_UNIT_REQUIRED)
    private Integer businessUnitId;

    @NotNull(message = Constants.DEPARTMENT_REQUIRED)
    private Integer departmentId;

    @NotNull(message = Constants.ROLE_REQUIRED)
    private Integer roleId;
    
    
}