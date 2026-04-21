package com.hms.service.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_assign_roles")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssignRolesEntity {
	
	
	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    @Column(name = "id", nullable = false, updatable = false)
	    private Integer id;

	    @SequenceGenerator(name = "assign_role_seq_gen", sequenceName = "assign_role_seq", allocationSize = 1)
	    @Column(name = "assign_role_id", unique = true, nullable = false)
	    private Integer assignRoleId;
	    
	    @Column(name = "user_id", nullable = false)
	    private Integer userId;
	    
	    @Column(name = "role_id", nullable = false)
	    private Integer roleId;
	    
	    @Column(name = "assigned_by")
	    private String assignedBy;

	    @Column(name = "assigned_at")
	    private LocalDate assignedAt;
}
