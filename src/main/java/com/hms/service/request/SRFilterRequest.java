package com.hms.service.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SRFilterRequest {

	@NotNull(message = "page is required")
	private Integer page;

	@NotNull(message = "size is required")
	private Integer size;

}
