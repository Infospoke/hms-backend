package com.hms.service.request;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KanbanFilterRequest {

	private Map<String, Object> filters;
	
	
}
