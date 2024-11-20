package com.trmo.floracare.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Entity
public class Schedule {
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;
    private LocalDate wateringDate;
    private Boolean isWatered = false;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plant_id", nullable = false)
    private Plant plant;
}
