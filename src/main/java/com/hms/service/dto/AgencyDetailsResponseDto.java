package com.hms.service.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AgencyDetailsResponseDto {

	private Integer id;
	private String agencyName;
	private String emailId;
	private List<CategoryResponseDto> categories;

}
