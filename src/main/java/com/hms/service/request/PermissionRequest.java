package com.hms.service.request;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class PermissionRequest {

	
	private LocalDateTime createdDate;

	private String createdBy;

	private String updatedBy;

	private LocalDateTime updatedDate;
	
	private List<ModulePermissionRequest> modules;

}
