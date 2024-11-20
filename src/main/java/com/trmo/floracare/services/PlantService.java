package com.trmo.floracare.services;

import com.trmo.floracare.entities.Plant;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface PlantService {
    Plant save(Plant room);

    Map<LocalDate, List<String>> getWateringScheduleForUser(String userId);

    void markWateringForDate(String userId, LocalDate date);
}
