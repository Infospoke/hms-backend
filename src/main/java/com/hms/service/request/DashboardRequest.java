package com.hms.service.request;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardRequest {
 
 private LocalDateTime fromDate;

 private LocalDateTime toDate;

 private String monthCode;

}
 