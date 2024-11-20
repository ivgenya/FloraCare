package com.trmo.floracare.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Entity
public class User {
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;
    private String username;
    private String email;
    private String passwordHash;
    private String googleId;
    private String avatarUrl;
    private LocalDateTime createdAt = LocalDateTime.now();
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("rooms")
    private List<Room> rooms;

}
