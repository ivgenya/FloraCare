package com.trmo.floracare.services.impl;

import com.trmo.floracare.dto.UserStatsDTO;
import com.trmo.floracare.entities.Device;
import com.trmo.floracare.entities.Room;
import com.trmo.floracare.entities.User;
import com.trmo.floracare.repositories.RoomRepository;
import com.trmo.floracare.services.RoomService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class RoomServiceImpl implements RoomService {
    @Autowired
    private RoomRepository repository;

    @Override
    public Room save(Room room) {
        return repository.save(room);
    }

    @Override
    public void delete(UUID roomID) {
        Optional<Room> room = repository.findById(roomID);
        room.ifPresent(value -> repository.delete(value));
    }

    @Override
    public List<Room> findByUser(User user) {
        return repository.findByUser(user);
    }

    @Override
    public Optional<Room> findById(UUID id) {
        return repository.findById(id);
    }


    @Override
    public UserStatsDTO statsByUser(User user) {
        UserStatsDTO dto = new UserStatsDTO();
        List<Room> rooms = repository.findByUser(user);
        dto.setRoomsCount(rooms.size());
        dto.setPlantsCount(rooms.stream()
                .mapToInt(room -> room.getPlants().size())
                .sum());
        dto.setDevicesCount(rooms.stream()
                .mapToInt(room -> room.getDevices().size())
                .sum());
        return dto;
    }

    @Override
    public Room linkDeviceToRoom(UUID roomId, String macAddress, String name) {
        Room room = repository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Room not found with ID: " + roomId));
        log.info(String.valueOf(room));
        Device device = new Device();
        device.setMacAddress(macAddress);
        device.setName(name);
        device.setRoom(room);
        if (!room.getDevices().isEmpty()) {
            throw new IllegalArgumentException("This device is already linked to the room.");
        }
        room.getDevices().add(device);
        return repository.save(room);
    }
}
