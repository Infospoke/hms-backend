package com.hms.service.response;



import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModuleResponse {
	
	private Integer moduleId;
	
	private String moduleName;
	
	private List<ModuleResponse> subModules;

}
