package com.trmo.floracare.services.impl;

import com.trmo.floracare.entities.Room;
import com.trmo.floracare.entities.User;
import com.trmo.floracare.repositories.RoomRepository;
import com.trmo.floracare.services.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RoomServiceImpl implements RoomService {
    @Autowired
    private RoomRepository repository;

    @Override
    public Room save(Room room) {
        return repository.save(room);
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
    public List<Room> getAll() {
        return repository.findAll();
    }
}
