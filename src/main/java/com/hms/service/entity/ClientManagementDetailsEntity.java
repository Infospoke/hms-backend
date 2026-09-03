package com.hms.service.entity;

import java.time.LocalDate;
import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.hms.service.request.PocConfig;

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
@Table(name = "tb_clients")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientManagementDetailsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Integer id;

    @Column(name = "client_name")
    private String clientName;

    @Column(name = "industry")
    private String industry;

    @Column(name = "team_size")
    private Integer teamSize;

    @Column(name = "client_status")
    private String clientStatus;

    @Column(name = "agreement_status")
    private String agreementStatus;

    @Column(name = "agreement_start_date")
    private LocalDate agreementStartDate;

    @Column(name = "agreement_end_date")
    private LocalDate agreementEndDate;

    @Column(name = "bdm")
    private String bdm;

    @Column(name = "business_proposed")
    private String businessProposed;

    @Column(name = "client_manager")
    private String clientManager;

    @Column(name = "designation")
    private String designation;

    @Column(name = "contact_no")
    private String contactNo;

    @Column(name = "email")
    private String email;

    @Column(name = "location")
    private String location;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "poc", columnDefinition = "jsonb")
    private List<PocConfig> poc;


    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;
}