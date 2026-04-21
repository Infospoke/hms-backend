package com.hms.service.request;


import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class PermissionRequest {

	private String createdBy;

	private String updatedBy;

	private List<ModulePermissionRequest> modules;

}
