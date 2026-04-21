package com.hms.service.request;

import java.time.LocalDateTime;

import com.hms.service.constants.Constants;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModuleRequest {

	@NotBlank(message=Constants.MODULE_REQUIRED)
	private String moduleName;

	private LocalDateTime createdDate;

	private String createdBy;

	private String updatedBy;

	private LocalDateTime updatedDate;

}
