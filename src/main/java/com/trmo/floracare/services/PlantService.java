package com.trmo.floracare.services;

import com.trmo.floracare.entities.Plant;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface PlantService {
    Plant save(Plant room);

    Plant update(Plant plant);

    Optional<Plant> findById(UUID id);

    Map<LocalDate, List<String>> getWateringScheduleForUser(String userId);

    void markWateringForDate(String userId, LocalDate date);

    void delete(UUID plantId);
}
