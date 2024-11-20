package com.trmo.floracare.services.impl;

import com.trmo.floracare.entities.*;
import com.trmo.floracare.repositories.PlantPhotoRepository;
import com.trmo.floracare.repositories.PlantRepository;
import com.trmo.floracare.repositories.RoomRepository;
import com.trmo.floracare.repositories.ScheduleRepository;
import com.trmo.floracare.services.PlantService;
import com.trmo.floracare.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class PlantServiceImpl implements PlantService {
    @Autowired
    private PlantRepository repository;
    @Autowired
    private PlantPhotoRepository photoRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private UserService userService;

    @Override
    public Plant save(Plant plant) {
        Plant savedPlant = repository.save(plant);
        for (PlantPhoto photo : savedPlant.getPhotos()) {
            photo.setPlant(savedPlant);
            photoRepository.save(photo);
        }
        createWateringSchedule(savedPlant);
        return savedPlant;
    }

    @Override
    public Map<LocalDate, List<String>> getWateringScheduleForUser(String userId) {
        Optional<User> optionalUser = userService.findById(UUID.fromString(userId));
        if (optionalUser.isEmpty()) {
            return null;
        }

        List<Room> rooms = roomRepository.findByUser(optionalUser.get());
        List<Plant> plants = rooms.stream()
                .flatMap(room -> room.getPlants().stream())
                .toList();

        Map<LocalDate, List<String>> wateringScheduleMap = new TreeMap<>();
        for (Plant plant : plants) {
            List<Schedule> schedules = scheduleRepository.findByPlantId(plant.getId());
            for (Schedule schedule : schedules) {
                if (!schedule.getIsWatered()) {
                    LocalDate wateringDate = schedule.getWateringDate();
                    String plantName = plant.getName();

                    wateringScheduleMap
                            .computeIfAbsent(wateringDate, k -> new ArrayList<>())
                            .add(plantName);
                }
            }
        }
        return wateringScheduleMap;
    }


    private void createWateringSchedule(Plant plant) {
        if (plant.getWateringFrequency() == null || plant.getWateringFrequency() <= 0) {
            return;
        }
        LocalDate today = LocalDate.now();
        List<Schedule> schedules = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            LocalDate wateringDate = today.plusDays((long) i * plant.getWateringFrequency());
            Schedule schedule = new Schedule();
            schedule.setPlant(plant);
            schedule.setWateringDate(wateringDate);
            schedule.setIsWatered(false);
            schedules.add(schedule);
        }
        scheduleRepository.saveAll(schedules);
    }

    @Override
    public void markWateringForDate(String userId, LocalDate date) {
        Optional<User> optionalUser = userService.findById(UUID.fromString(userId));
        if (optionalUser.isEmpty()) {
            return;
        }
        List<Room> rooms = roomRepository.findByUser(optionalUser.get());
        List<Plant> plants = rooms.stream()
                .flatMap(room -> room.getPlants().stream())
                .toList();
        if (!plants.isEmpty()) {
            for (Plant plant : plants) {
                List<Schedule> schedules = plant.getSchedules();
                for (Schedule schedule : schedules) {
                    if (schedule.getWateringDate().equals(date)) {
                        schedule.setIsWatered(true);
                    }
                }
                scheduleRepository.saveAll(schedules);
            }
        }
    }
}
