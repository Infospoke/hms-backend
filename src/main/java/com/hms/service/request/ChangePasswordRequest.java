package com.hms.service.request;

import java.time.LocalDateTime;

import com.hms.service.entity.PasswordHistoryEntity;
import com.hms.service.enums.CredentialType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordRequest {
    private String oldPassword;
    private String newPassword;
    private String oldPin;
    private String newPin;

}
