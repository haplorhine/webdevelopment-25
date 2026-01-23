package at.technikum.springrestbackend.services;

import at.technikum.springrestbackend.dto.EventDto;
import at.technikum.springrestbackend.entity.EventEntity;
import at.technikum.springrestbackend.entity.ImageEntity;
import at.technikum.springrestbackend.entity.UserEntity;
import at.technikum.springrestbackend.mapper.EventMapper;
import at.technikum.springrestbackend.repositories.EventRepository;
import at.technikum.springrestbackend.repositories.ImageRepository;
import at.technikum.springrestbackend.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private ImageRepository imageRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EventMapper eventMapper;

    @InjectMocks
    private EventService eventService;

    private UUID eventId;
    private UUID hostId;
    private UUID imageId;
    private UserEntity host;
    private ImageEntity image;
    private EventEntity eventEntity;
    private EventDto eventDto;

    @BeforeEach
    void setUp() {
        eventId = UUID.randomUUID();
        hostId = UUID.randomUUID();
        imageId = UUID.randomUUID();
        host = TestDataFactory.createUser(hostId);
        image = TestDataFactory.createImage(imageId);
        eventEntity = TestDataFactory.createEvent(eventId, host);
        eventDto = TestDataFactory.createEventDto(hostId, imageId);
    }

    @Test
    void getEvents_returnsMappedDtos() {
        EventEntity anotherEntity = TestDataFactory.createEvent(UUID.randomUUID(), host);
        EventDto anotherDto = TestDataFactory.createEventDto(hostId, null);
        when(eventRepository.findAll()).thenReturn(List.of(eventEntity, anotherEntity));
        when(eventMapper.toDto(eventEntity)).thenReturn(eventDto);
        when(eventMapper.toDto(anotherEntity)).thenReturn(anotherDto);

        List<EventDto> result = eventService.getEvents();

        assertThat(result).containsExactly(eventDto, anotherDto);
        verify(eventRepository).findAll();
        verify(eventMapper, times(2)).toDto(any(EventEntity.class));
    }

    @Test
    void createEvent_whenHostMissing_throwsNotFound() {
        when(userRepository.findById(hostId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.createEvent(eventDto))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
        verify(eventRepository, never()).save(any());
    }

    @Test
    void createEvent_withImageButMissingEntity_throwsNotFound() {
        when(userRepository.findById(hostId)).thenReturn(Optional.of(host));
        when(eventMapper.toEntity(eventDto)).thenReturn(eventEntity);
        when(imageRepository.findById(imageId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.createEvent(eventDto))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Image not found")
                .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
        verify(eventRepository, never()).save(any());
    }

    @Test
    void createEvent_withoutImage_persistsEvent() {
        eventDto.setImageId(null);
        when(userRepository.findById(hostId)).thenReturn(Optional.of(host));
        when(eventMapper.toEntity(eventDto)).thenReturn(eventEntity);
        when(eventRepository.save(eventEntity)).thenReturn(eventEntity);
        when(eventMapper.toDto(eventEntity)).thenReturn(eventDto);

        EventDto result = eventService.createEvent(eventDto);

        assertThat(result).isEqualTo(eventDto);
        assertThat(eventEntity.getHost()).isEqualTo(host);
        verify(imageRepository, never()).findById(any());
        verify(eventRepository).save(eventEntity);
    }

    @Test
    void createEvent_withImage_setsAssociation() {
        when(userRepository.findById(hostId)).thenReturn(Optional.of(host));
        when(eventMapper.toEntity(eventDto)).thenReturn(eventEntity);
        when(imageRepository.findById(imageId)).thenReturn(Optional.of(image));
        when(eventRepository.save(eventEntity)).thenReturn(eventEntity);
        when(eventMapper.toDto(eventEntity)).thenReturn(eventDto);

        EventDto result = eventService.createEvent(eventDto);

        assertThat(result).isEqualTo(eventDto);
        assertThat(eventEntity.getImage()).isEqualTo(image);
        verify(eventRepository).save(eventEntity);
    }

    @Test
    void deleteEventById_delegatesToRepository() {
        eventService.deleteEventById(eventId);
        verify(eventRepository).deleteById(eventId);
    }

    @Test
    void updateEvent_whenEventMissing_throwsNotFound() {
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.updateEvent(eventId, eventDto))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void updateEvent_whenHostMissing_throwsNotFound() {
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(eventEntity));
        when(userRepository.findById(hostId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.updateEvent(eventId, eventDto))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
        verify(eventRepository, never()).save(any());
    }

    @Test
    void updateEvent_whenImageMissing_throwsNotFound() {
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(eventEntity));
        when(userRepository.findById(hostId)).thenReturn(Optional.of(host));
        when(imageRepository.findById(imageId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.updateEvent(eventId, eventDto))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Image not found")
                .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
        verify(eventRepository, never()).save(any());
    }

    @Test
    void updateEvent_successfullyUpdatesAndReturnsDto() {
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(eventEntity));
        when(userRepository.findById(hostId)).thenReturn(Optional.of(host));
        when(imageRepository.findById(imageId)).thenReturn(Optional.of(image));
        when(eventRepository.save(eventEntity)).thenReturn(eventEntity);
        when(eventMapper.toDto(eventEntity)).thenReturn(eventDto);

        EventDto result = eventService.updateEvent(eventId, eventDto);

        assertThat(result).isEqualTo(eventDto);
        verify(eventMapper).updateEntityFromDto(eventDto, eventEntity);
        assertThat(eventEntity.getHost()).isEqualTo(host);
        assertThat(eventEntity.getImage()).isEqualTo(image);
        verify(eventRepository).save(eventEntity);
    }

    @Test
    void getEventById_whenEventMissing_throwsNotFound() {
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.getEventById(eventId))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void getEventById_returnsMappedDto() {
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(eventEntity));
        when(eventMapper.toDto(eventEntity)).thenReturn(eventDto);

        EventDto result = eventService.getEventById(eventId);

        assertThat(result).isEqualTo(eventDto);
        verify(eventRepository).findById(eventId);
    }
}
