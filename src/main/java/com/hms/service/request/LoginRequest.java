package com.hms.service.request;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
    private String pin; 
}
