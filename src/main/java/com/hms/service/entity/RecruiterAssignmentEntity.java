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
@Table(name = "tb_recruiter_assignment")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecruiterAssignmentEntity {
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false, updatable = false)
	private Integer id;
	
	@Column(name="user_id")
	private Integer userId;
	
	@Column(name="sr_id")
	private String srId;
	
	@Column(name="user_name")
	private String userName;
	
	@Column(name="email")
	private String email;
	
	@Column(name="role_name")
	private String roleName;
	
	@Column(name="role_id")
	private Integer roleId;
	
	@Column(name="status")
	private String status;
	
	@Column(name="comments")
	private String comments;
 
    @Column(name="assigned_by")
    private String assignedBy;
    
    @Column(name="assigned_at")
    private LocalDateTime assignedAt;
    
    @Column(name = "responded_at")
    private LocalDateTime respondedAt;
    
    @Column(name = "job_id")
    private Integer jobId;
    
 
}

