package com.trmo.floracare.repositories;

import com.trmo.floracare.entities.Room;
import com.trmo.floracare.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RoomRepository extends JpaRepository<Room, UUID> {
    List<Room> findByUser(User user);
}
