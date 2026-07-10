package com.hms.service.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OfferCommentsResponse {

    private String role;
    private String approverName;
    private Boolean approved;
    private LocalDateTime approvedOn;
    private String comments;
}
