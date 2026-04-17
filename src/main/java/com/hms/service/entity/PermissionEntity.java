package com.hms.service.entity;

import java.time.LocalDateTime;

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
@Table(name = "tb_permission")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PermissionEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false, updatable = false)
	private Integer id;
	

    @SequenceGenerator(
            name = "permission_seq_gen",
            sequenceName = "permission_seq",
            allocationSize = 1
    )    
	@Column(name = "permission_id")
    private Integer permissionId;
	
	
	@Column(name="module_id")
	private Integer moduleId;
	
	@Column(name="role_id")
	private Integer roleId;
	
	@Column(name="can_create")
	private Boolean create=false;

   @Column(name="can_view")
   private Boolean view=false;
   
   @Column(name="can_edit")
   private Boolean edit=false;
   
   @Column(name="can_delete")
   private Boolean delete=false;
   
   @Column(name = "created_date")
	private LocalDateTime createdDate;
	
	@Column(name="created_by")
	private String createdBy;
	
	@Column(name="updated_by")
	private String updatedBy;
	
	@Column(name="updated_date")
	private LocalDateTime updatedDate;
	
}
