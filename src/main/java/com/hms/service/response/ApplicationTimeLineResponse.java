package com.hms.service.response;

import java.time.LocalDateTime;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor

public class ApplicationTimeLineResponse {
    private String roundName;
    private LocalDateTime completedDate;
    private LocalDateTime scheduledDate;

}
