package com.trmo.floracare;

import com.trmo.floracare.entities.*;
import com.trmo.floracare.repositories.PlantPhotoRepository;
import com.trmo.floracare.repositories.PlantRepository;
import com.trmo.floracare.repositories.RoomRepository;
import com.trmo.floracare.repositories.ScheduleRepository;
import com.trmo.floracare.services.UserService;
import com.trmo.floracare.services.impl.PlantServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PlantServiceTest {

    @Mock
    private PlantRepository plantRepository;
    @Mock
    private PlantPhotoRepository plantPhotoRepository;
    @Mock
    private RoomRepository roomRepository;
    @Mock
    private ScheduleRepository scheduleRepository;
    @Mock
    private UserService userService;

    @InjectMocks
    private PlantServiceImpl plantService;

    private Plant plant;
    private Room room;
    private User user;
    private Schedule schedule;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(UUID.randomUUID());

        room = new Room();
        room.setUser(user);
        room.setPlants(new ArrayList<>());

        PlantPhoto photo = new PlantPhoto();
        photo.setId(UUID.randomUUID());

        plant = new Plant();
        plant.setId(UUID.randomUUID());
        plant.setName("Фикус");
        plant.setWateringFrequency(2);
        plant.setPhotos(Collections.singletonList(photo));

        schedule = new Schedule();
        schedule.setWateringDate(LocalDate.now().plusDays(2));
        schedule.setIsWatered(false);
        plant.setSchedules(Collections.singletonList(schedule));
    }

    @Test
    @DisplayName("Должен успешно сохранить растение")
    public void testSavePlant() {
        // given
        when(plantRepository.save(any(Plant.class))).thenReturn(plant);
        when(plantPhotoRepository.save(any(PlantPhoto.class))).thenReturn(new PlantPhoto());

        // when
        Plant savedPlant = plantService.save(plant);

        // then
        verify(plantRepository).save(plant);
        verify(plantPhotoRepository).save(any(PlantPhoto.class));
        assertNotNull(savedPlant);
        assertEquals(savedPlant.getName(), "Фикус");
    }

    @Test
    @DisplayName("Должен успешно найти растение по идентификатору")
    public void testFindPlantById() {
        // given
        when(plantRepository.findById(any(UUID.class))).thenReturn(Optional.of(plant));

        // when
        Optional<Plant> actual = plantService.findById(plant.getId());

        // then
        verify(plantRepository).findById(any(UUID.class));
        assertTrue(actual.isPresent());
        assertEquals(plant.getId(), actual.get().getId());
    }

    @Test
    @DisplayName("Должен успешно обновить растение")
    public void testUpdatePlant() {
        // given
        when(plantRepository.save(any(Plant.class))).thenReturn(plant);

        // when
        plantService.update(plant);

        // then
        verify(plantRepository).save(any(Plant.class));
    }

    @Test
    @DisplayName("Должен успешно вернуть расписание полива")
    public void testGetWateringScheduleForUser() {
        // given
        when(userService.findById(any(UUID.class))).thenReturn(Optional.of(user));
        when(roomRepository.findByUser(any(User.class))).thenReturn(Collections.singletonList(room));
        when(scheduleRepository.findByPlantId(any(UUID.class))).thenReturn(Collections.singletonList(schedule));

        room.getPlants().add(plant);

        // when
        Map<LocalDate, List<String>> scheduleMap = plantService.getWateringScheduleForUser(user.getId().toString());

        // then
        assertNotNull(scheduleMap);
        assertTrue(scheduleMap.containsKey(schedule.getWateringDate()));
        assertTrue(scheduleMap.get(schedule.getWateringDate()).contains(plant.getName()));
    }

    @Test
    @DisplayName("Должен отметить полив в указанный день")
    public void testMarkWateringForDate() {
        // given
        when(userService.findById(any(UUID.class))).thenReturn(Optional.of(user));
        when(roomRepository.findByUser(any(User.class))).thenReturn(Collections.singletonList(room));

        room.getPlants().add(plant);

        // when
        plantService.markWateringForDate(user.getId().toString(), LocalDate.now().plusDays(2));

        // then
        verify(scheduleRepository).saveAll(anyList());
        assertTrue(schedule.getIsWatered());
    }

    @Test
    @DisplayName("Должен успешно удалить растение")
    public void testDeletePlant() {
        // given
        when(plantRepository.findById(any(UUID.class))).thenReturn(Optional.of(plant));

        // when
        plantService.delete(plant.getId());

        // then
        verify(plantRepository, times(1)).delete(any(Plant.class));
    }

}
