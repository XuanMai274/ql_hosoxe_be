package com.bidv.asset.vehicle.entity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "vehicle_dossiers")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class VehicleDossierEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "vehicle_dossier_id_seq")
    @SequenceGenerator(name = "vehicle_dossier_id_seq", sequenceName = "vehicle_dossier_id_seq")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    private VehicleEntity vehicle;

    private String status;
    private LocalDate importDate;
    private LocalDate exportDate;
    private String originalCopy;
    private String importDocs;
    private String registrationOrderNumber;
    private LocalDate docsDeliveryDate;
    private LocalDateTime createdAt;
}
