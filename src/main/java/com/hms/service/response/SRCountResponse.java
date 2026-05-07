package com.hms.service.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SRCountResponse {

    private Long totalSrs;

    private Long approved;

    private Long rejected;

    private Long inProgress;
}
