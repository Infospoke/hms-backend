package com.hms.service.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HiringDashboardResponseDto {

	private HiringDashboardCardsDto cards;

	private List<MyAssignedJobsDto> myRequisitions;

}