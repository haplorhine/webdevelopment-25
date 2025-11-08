package at.technikum.springrestbackend.services;

import at.technikum.springrestbackend.dto.EventDto;
import at.technikum.springrestbackend.mapper.EventMapper;
import at.technikum.springrestbackend.repositories.EventRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    public EventService(EventRepository eventRepository, EventMapper eventMapper) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
    }

    public List<EventDto> getEvents() {
        return eventRepository.findAll().stream().map(eventMapper::toDto).toList();
    }

    public EventDto createEvent(EventDto eventDto) {
        return eventMapper.toDto(eventRepository.save(eventMapper.toEntity(eventDto)));
    }
}
