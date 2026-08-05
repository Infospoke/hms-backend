package com.hms.service.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HiringTrendDto {

	private LocalDate date;

	private Long candidatesAdded;

	private Long offersReleased;

	private Long hired;

}