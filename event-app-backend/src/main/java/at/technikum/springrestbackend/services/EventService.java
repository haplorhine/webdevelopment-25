package at.technikum.springrestbackend.services;
import at.technikum.springrestbackend.dto.EventDto;
import at.technikum.springrestbackend.entity.EventEntity;
import at.technikum.springrestbackend.entity.UserEntity;
import at.technikum.springrestbackend.mapper.EventMapper;
import at.technikum.springrestbackend.repositories.EventRepository;
import at.technikum.springrestbackend.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
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
}
