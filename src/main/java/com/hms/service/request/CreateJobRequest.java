package com.hms.service.request;


import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateJobRequest {

    private String additionalNotes;

    private List<Integer> channelIds;

    private String referralAmount;
}
