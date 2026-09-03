package com.hms.service.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PocConfig {

    @NotBlank(message = "POC name is required")
    private String pocName;

    @NotBlank(message = "Designation is required")
    private String designation;

    @NotBlank(message = "Contact number is required")
    private String contactNo;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Location is required")
    private String location;
}