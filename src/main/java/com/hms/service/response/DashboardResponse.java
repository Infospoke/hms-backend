package com.hms.service.response;


import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardResponse {
 
 private int onBoarding;
 private int offer_sent;
 private int offer_rejected;
 private int BGV_initated;
 private int BGV_Cleared;
 private int BGV_rejected;
 private int joined;
 private int accepted;
 private long totalCandidates;
 
 private Map<String, Long> reportMap;
}
 