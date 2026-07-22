package com.hms.service.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tb_category")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Integer id;

	@Column(name = "category_name", nullable = false, unique = true)
	private String categoryName;

}