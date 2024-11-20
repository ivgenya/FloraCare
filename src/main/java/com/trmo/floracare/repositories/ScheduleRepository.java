package com.trmo.floracare.repositories;

import com.trmo.floracare.entities.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ScheduleRepository extends JpaRepository<Schedule, UUID> {
    List<Schedule> findByPlantId(UUID plantId);
}
