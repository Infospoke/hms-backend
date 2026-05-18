package com.hms.service.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class SourcingChannelResponse {

    private Integer id;

    private String channelName;

    private String bestFor;

    private String cost;

    private Boolean postJob;

    private String referralAmount;
}