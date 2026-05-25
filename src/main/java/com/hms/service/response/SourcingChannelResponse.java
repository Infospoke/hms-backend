package com.hms.service.response;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SourcingChannelResponse {


    private Map<String, Boolean> sourcingChannels;

    private Boolean referral;

    private Double referralAmount;
}