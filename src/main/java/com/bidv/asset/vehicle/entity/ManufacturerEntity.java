package com.bidv.asset.vehicle.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "manufacturer")
@Getter @Setter
@NoArgsConstructor
public class ManufacturerEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "manufacturer_id_seq"
    )
    @SequenceGenerator(
            name = "manufacturer_id_seq",
            sequenceName = "manufacturer_id_seq"
    )
    private Long id;

    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @OneToMany(mappedBy = "manufacturer", fetch = FetchType.LAZY)
    private List<VehicleEntity> vehicles;
}
