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

@Table(name="tb_candidate_creation")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CandidateCreationDetailsEntity {
	
	  @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    @Column(name = "id", nullable = false, updatable = false)
	    private Integer id;
	  
		@Column(name = "first_name")
		private String firstName;

		@Column(name = "last_name")
		private String lastName;

		@Column(name = "phone_number")
		private String phoneNumber;

		@Column(name = "email")
		private String email;
		
		@Column(name = "password")
		private String password;
		
		@Column(name = "resume")
		private String resume;

		@Column(name = "additional_file")
		private String additionalFile;

}
