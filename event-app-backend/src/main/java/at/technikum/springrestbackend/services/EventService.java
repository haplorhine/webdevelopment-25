package at.technikum.springrestbackend.services;
import at.technikum.springrestbackend.dto.EventDto;
import at.technikum.springrestbackend.entity.EventEntity;
import at.technikum.springrestbackend.entity.UserEntity;
import at.technikum.springrestbackend.mapper.EventMapper;
import at.technikum.springrestbackend.repositories.EventRepository;
import at.technikum.springrestbackend.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventMapper eventMapper;

    public EventService(EventRepository eventRepository, UserRepository userRepository, EventMapper eventMapper) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.eventMapper = eventMapper;
    }

    public List<EventDto> getEvents() {
        return eventRepository.findAll().stream().map(eventMapper::toDto).toList();
    }

    public EventDto createEvent(EventDto eventDto) {
        UserEntity host = userRepository.findById(eventDto.getHostId()).orElse(null);
        EventEntity eventEntity = eventMapper.toEntity(eventDto);
        eventEntity.setHost(host);
        EventEntity savedEntity = eventRepository.save(eventEntity);
        return eventMapper.toDto(savedEntity);
    }

    public void deleteEventById(UUID id) {
        eventRepository.deleteById(id);
    }

    public EventDto updateEvent(UUID id, EventDto eventDto) {
        EventEntity savedEvent = eventRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        eventMapper.updateEntityFromDto(eventDto, savedEvent);
        UserEntity savedUser = userRepository.findById(eventDto.getHostId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        savedEvent.setHost(savedUser);
        eventRepository.save(savedEvent);
        return eventMapper.toDto(savedEvent);
    }

    public EventDto getEventById(UUID id) {
        Optional<EventEntity> eventOpt = eventRepository.findById(id);
        if (eventOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return eventMapper.toDto(eventOpt.get());
    }
}
