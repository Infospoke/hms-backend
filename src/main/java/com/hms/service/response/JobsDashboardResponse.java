package com.hms.service.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobsDashboardResponse {
	

	    private long openJobs;
	    private long candidates;
	    private long interviews;
	    private long offersCount;

	
}
