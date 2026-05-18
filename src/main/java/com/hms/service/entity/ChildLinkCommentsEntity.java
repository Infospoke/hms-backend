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
@Table(name = "tb_child_link_comments")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChildLinkCommentsEntity {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Integer id;
	
	  @Column(name = "chain_id")
	  private Integer chainId;
	  
	  @Column(name="comments")
	  private String comments;
	  
	  @Column(name="action")    
	  private String action;
	  
	  @Column(name="description")
	  private String description;

}
