package com.hms.service.request;


import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateJobRequest {

    private String additionalNotes;

    @Valid
    @NotEmpty(message = "At least one channel is required")
    private List<SourcingChannelRequest> channels;
}