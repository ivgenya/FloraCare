package com.trmo.floracare.repositories;

import com.trmo.floracare.entities.Plant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PlantRepository extends JpaRepository<Plant, UUID> {
}
