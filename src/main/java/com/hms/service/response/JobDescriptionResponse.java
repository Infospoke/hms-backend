package com.hms.service.response;

import java.util.List;

import com.hms.service.request.Description;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobDescriptionResponse {

	 private List<JobDescriptionDetailResponse> description;
}
