package com.trmo.floracare.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Entity
public class Plant {
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;
    private String name;
    private Integer minTemperature;
    private Integer maxTemperature;
    private Integer minHumidity;
    private Integer maxHumidity;
    private Integer wateringFrequency;
    private Integer fertilizingFrequency;
    private String description;
    private LocalDateTime createdAt = LocalDateTime.now();
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    @JsonIgnore
    private Room room;
    @OneToMany(mappedBy = "plant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlantPhoto> photos;
    @OneToMany(mappedBy = "plant", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Schedule> schedules;
}
