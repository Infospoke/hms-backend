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
@Table(name="tb_child_Approvals")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApprovalsChildEntity {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id",updatable=false,nullable=false)
	private Integer id;

	
	@Column(name="process_id")
	private String processId;
	
	@Column(name="process_name")
	private String processName;

	@Column(name="submitted_by")
	private Integer submittedBy;
	
	@Column(name="approver1")
	private Boolean approver1=false;
	
	@Column(name="approver2")
	private Boolean approver2=false;
	
	@Column(name="approver3")
	private Boolean approver3 = false;
	
	@Column(name="role1")
	private Integer role1;
	
	@Column(name="role2")
	private Integer role2;
	
	@Column(name="role3")
	private Integer role3;
	
	@Column(name="department")
	private Integer department;
	
	
	@Column(name="date_of_approval")
	private LocalDateTime dateOfApproval;

//	@Column(name="approved_by")
//	private String approvedBy;
//	
//	@Column(name="approved")
//	private Boolean approved;
//	
//	@Column(name="rejected")
//	private Boolean rejected;
//	
//	@Column(name="rejected_by")
//	private String rejectedBy;
}
