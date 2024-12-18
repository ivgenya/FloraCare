package com.trmo.floracare;

import com.trmo.floracare.dto.UserStatsDTO;
import com.trmo.floracare.entities.Device;
import com.trmo.floracare.entities.Plant;
import com.trmo.floracare.entities.Room;
import com.trmo.floracare.entities.User;
import com.trmo.floracare.repositories.RoomRepository;
import com.trmo.floracare.services.impl.RoomServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RoomServiceTest {
    @InjectMocks
    private RoomServiceImpl roomService;
    @Mock
    private RoomRepository roomRepository;

    private User user;
    private Room room;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(UUID.randomUUID());

        room = new Room();
        room.setId(UUID.randomUUID());
        room.setUser(user);
        room.setDevices(new ArrayList<>());
        room.setPlants(new ArrayList<>());
    }

    @Test
    @DisplayName("Должен успешно сохранить комнату")
    void testSaveRoom() {
        // given
        when(roomRepository.save(room)).thenReturn(room);

        // when
        Room savedRoom = roomService.save(room);

        // then
        assertNotNull(savedRoom);
        verify(roomRepository).save(room);
    }

    @Test
    @DisplayName("Должен успешно удалить комнату")
    void testDeleteRoom() {
        // given
        when(roomRepository.findById(room.getId())).thenReturn(Optional.of(room));

        // when
        roomService.delete(room.getId());

        // then
        verify(roomRepository).delete(room);
    }

    @Test
    @DisplayName("Не должен удалять комнату, если она не найдена")
    void testDeleteRoomNotFound() {
        // given
        when(roomRepository.findById(room.getId())).thenReturn(Optional.empty());

        // when
        roomService.delete(room.getId());

        // then
        verify(roomRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Должен успешно найти комнаты пользователя")
    void testFindByUser() {
        // given
        List<Room> rooms = List.of(room);
        when(roomRepository.findByUser(user)).thenReturn(rooms);

        // when
        List<Room> foundRooms = roomService.findByUser(user);

        // then
        assertNotNull(foundRooms);
        assertEquals(1, foundRooms.size());
        assertEquals(room, foundRooms.get(0));
    }

    @Test
    @DisplayName("Должен успешно найти комнату по идентификатору")
    void testFindById() {
        // given
        when(roomRepository.findById(room.getId())).thenReturn(Optional.of(room));

        // when
        Optional<Room> foundRoom = roomService.findById(room.getId());

        // then
        assertTrue(foundRoom.isPresent());
        assertEquals(room, foundRoom.get());
    }

    @Test
    @DisplayName("Должен успешно вернуть статистику пользователя")
    void testStatsByUser() {
        // given
        Room room = new Room();
        room.setPlants(new ArrayList<>(List.of(new Plant(), new Plant())));
        room.setDevices(new ArrayList<>(List.of(new Device())));

        Room anotherRoom = new Room();
        anotherRoom.setPlants(new ArrayList<>(List.of(new Plant())));
        anotherRoom.setDevices(new ArrayList<>(List.of(new Device())));

        when(roomRepository.findByUser(user)).thenReturn(List.of(room, anotherRoom));

        // then
        UserStatsDTO stats = roomService.statsByUser(user);

        // when
        verify(roomRepository).findByUser(eq(user));
        assertEquals(2, stats.getRoomsCount());
        assertEquals(3, stats.getPlantsCount());
        assertEquals(2, stats.getDevicesCount());
    }

    @Test
    @DisplayName("Должен успешно привязать устройство к комнате")
    void testLinkDeviceToRoom() {
        // given
        String macAddress = "00:1B:44:11:3A:B7";
        String name = "Test Device";

        when(roomRepository.findById(room.getId())).thenReturn(Optional.of(room));
        when(roomRepository.save(any())).thenAnswer(new Answer<Room>() {
            @Override
            public Room answer(InvocationOnMock invocation) {
                return invocation.getArgument(0);
            }
        });

        // when
        Room updatedRoom = roomService.linkDeviceToRoom(room.getId(), macAddress, name);

        // then
        verify(roomRepository, times(1)).save(room);
        assertNotNull(updatedRoom);
        assertEquals(1, updatedRoom.getDevices().size());
        assertEquals(macAddress, updatedRoom.getDevices().get(0).getMacAddress());
        assertEquals(name, updatedRoom.getDevices().get(0).getName());
    }

    @Test
    @DisplayName("Должен выбростить исключение, если комната не найдена при привязке устройства")
    void testLinkDeviceToRoomRoomNotFound() {
        // given
        when(roomRepository.findById(room.getId())).thenReturn(Optional.empty());

        // when
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                roomService.linkDeviceToRoom(room.getId(), "00:1B:44:11:3A:B7", "Test Device"));

        // then
        assertEquals("Room not found with ID: " + room.getId(), exception.getMessage());
    }

    @Test
    @DisplayName("Должен выбростить исключение, если к комнате уже привязано устройство")
    void testLinkDeviceToRoomDeviceAlreadyExists() {
        // given
        Device existingDevice = new Device();
        room.getDevices().add(existingDevice);

        when(roomRepository.findById(room.getId())).thenReturn(Optional.of(room));

        // when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                roomService.linkDeviceToRoom(room.getId(), "00:1B:44:11:3A:B7", "Test Device"));

        // then
        assertEquals("This device is already linked to the room.", exception.getMessage());
    }
}
