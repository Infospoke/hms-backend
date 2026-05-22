package com.hms.service.entity;

import java.util.Map;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;


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
    
    @Column(name = "sr_id")
    private String srId;
    
    @Column(name = "job_id")
    private Integer jobId;

    @JdbcTypeCode(SqlTypes.JSON)

	@Column(name = "channel_config", columnDefinition = "json")

	private Map<String, Boolean> sourcingChannelRequest;
    
    @Column(name="referral")
    private Boolean referral;
    
    @Column(name="referral_amount")
    private double referralAmount;
	    
}