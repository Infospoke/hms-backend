package com.hms.service.request;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModuleRequest {

	private String moduleName;

	private LocalDateTime createdDate;

	private String createdBy;

	private String updatedBy;

	private LocalDateTime updatedDate;

}
