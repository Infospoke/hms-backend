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
	@Table(name = "tb_offer_letter_template")
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public class OfferLetterTemplateEntity {

	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Integer id;

	    @Column(name = "offer_letter_template")
	    private String offerLetterTemplate;

}
