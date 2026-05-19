package com.hms.service.entity;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_job_posting")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobPostingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "job_code")
    private String jobCode;
    
    @Column(name = "job_id")
    private Integer jobId;

    @Column(name = "sourcing_channel_id")
    private Integer sourcingChannelId;

    @Column(name = "post_job")
    private Boolean postJob=false;

    @Column(name = "referral_amount")
    private String referralAmount;

}