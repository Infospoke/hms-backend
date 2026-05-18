package com.hms.service.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SourcingChannelRequest {

    @NotBlank(message = "Channel name is required")
    private String channelName;

    @NotNull(message = "Post Job toggle is required")
    private Boolean postJob;

    private String referralAmount;
}