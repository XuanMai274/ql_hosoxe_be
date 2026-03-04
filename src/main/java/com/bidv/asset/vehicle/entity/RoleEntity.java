package com.bidv.asset.vehicle.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "role")
@Getter
@Setter
@NoArgsConstructor
public class RoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_id_seq")
    @SequenceGenerator(name = "role_id_seq", sequenceName = "role_id_seq", allocationSize = 1)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    private String name;

    @OneToMany(mappedBy = "role")
    private List<UserAccountEntity> users;
}