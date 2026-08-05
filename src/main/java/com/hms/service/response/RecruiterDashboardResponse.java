package com.hms.service.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecruiterDashboardResponse {

    private RecruiterDashboardCountResponse dashboardCounts;

    private List<RecruiterAssignmentDashboardResponse> assignments;

}