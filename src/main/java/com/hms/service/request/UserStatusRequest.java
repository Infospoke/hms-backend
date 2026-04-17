package com.hms.service.request;

import java.time.LocalDateTime;

import com.hms.service.enums.UserStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserStatusRequest {
	private Integer id;
    private UserStatus status;
    private String updatedBy;
    private LocalDateTime updatedAt;
}