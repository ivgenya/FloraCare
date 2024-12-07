package com.trmo.floracare.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Data
@Entity
public class PlantPhoto {
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;
    private String image;
    private boolean isMain;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plant_id", nullable = false)
    @JsonIgnore
    private Plant plant;
}
