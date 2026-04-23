package com.hms.service.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tb_user_type")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserTypeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Integer id;

    @Column(name = "user_type")
    private String userType;
}