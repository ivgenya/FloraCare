package com.trmo.floracare.repositories;

import com.trmo.floracare.entities.PlantPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PlantPhotoRepository extends JpaRepository<PlantPhoto, UUID> {
}
