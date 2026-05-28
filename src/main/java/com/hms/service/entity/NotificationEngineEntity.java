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
@Table(name = "tb_notification_engine")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class NotificationEngineEntity {
	
	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    @Column(name = "id", updatable = false)
	    private Integer id;
	   
	    @Column(name = "notification_title")
	    private String notificationTitle;
	    
	    @Column(name = "message")
	    private String message;
	    
	    @Column(name = "process_id")
	    private String processId;
	    
	    @Column(name="role_id")
	    private Integer roleId;
	    
	    @Column(name = "dept_name")
	    private String deptName;
	    
	    @Column(name = "role_name")
	    private String roleName;
	    
	    @Column(name="dept_id")
	    private Integer deptId;
	    
	    @Column(name = "notification_sent_at")
	    private LocalDateTime notificationSentAt;
	    
	    @Column(name = "is_read")
	    private Boolean isRead=false;
	    
	    @Column (name="sent")
	    private Boolean sent=false;

}
