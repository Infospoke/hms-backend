package com.hms.service.response;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserListResponse {

    private List<UserResponse> users;

    private Long totalCount;        
    private Long activeCount;       
    private Long deactivatedCount; 

    private Long filteredCount; // when roleId present

    private List<Map<String, Object>> roleCounts; // when roleId null
}