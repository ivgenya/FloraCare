package com.trmo.floracare.services;

import com.trmo.floracare.entities.Room;
import com.trmo.floracare.entities.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoomService {
    List<Room> getAll();

    List<Room> findByUser(User user);

    Optional<Room> findById(UUID id);

    Room save(Room room);
}
