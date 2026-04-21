package com.hms.service.response;

import java.util.List;

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
    private Long filteredCount;    
}