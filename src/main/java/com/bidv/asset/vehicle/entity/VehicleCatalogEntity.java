package com.bidv.asset.vehicle.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "vehicle_catalog")
public class VehicleCatalogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "vehicle_catalog_id_seq")
    @SequenceGenerator(name = "vehicle_catalog_id_seq", sequenceName = "vehicle_catalog_id_seq", allocationSize = 1)
    private Long id;

    @Column(name = "model_name", nullable = false, unique = true)
    private String modelName;

    @Column(name = "seats")
    private Integer seats;

    @Column(name = "description")
    private String description;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public VehicleCatalogEntity() {
    }

    public VehicleCatalogEntity(String modelName, Integer seats, String description) {
        this.modelName = modelName;
        this.seats = seats;
        this.description = description;
        this.createdAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Manual Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public Integer getSeats() {
        return seats;
    }

    public void setSeats(Integer seats) {
        this.seats = seats;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
