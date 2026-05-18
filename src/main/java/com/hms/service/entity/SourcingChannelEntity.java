package com.hms.service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_sourcing_channel")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SourcingChannelEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "job_id")
    private Integer jobId;

    @Column(name = "channel_name")
    private String channelName;

    @Column(name = "best_for")
    private String bestFor;

    @Column(name = "cost")
    private String cost;
    
    @Column(name = "post_job")
    private Boolean postJob=false;
    
    @Column(name = "referral_amount")
    private String referralAmount;
}