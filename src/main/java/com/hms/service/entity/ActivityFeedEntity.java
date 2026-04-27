package com.hms.service.entity;

import java.time.LocalDateTime;

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
@Table(name="tb_activity_feed")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActivityFeedEntity {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name ="id",insertable = false , updatable = false)
	private Integer id;
	
	@Column(name="timestamp")
	private LocalDateTime timeStamp;
	
	@Column(name="activity")
	private String activity;
	

}
