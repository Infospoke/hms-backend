package com.hms.service.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecuriterDashboardDetailsDto {

    private DashboardCardsDto cards;

    private List<MyAssignedJobsDto> myAssignedJobsDto;

}