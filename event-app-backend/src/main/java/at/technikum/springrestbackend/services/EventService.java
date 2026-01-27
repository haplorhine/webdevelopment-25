package at.technikum.springrestbackend.services;

import at.technikum.springrestbackend.dto.EventDto;
import at.technikum.springrestbackend.entity.EventEntity;
import at.technikum.springrestbackend.entity.ImageEntity;
import at.technikum.springrestbackend.entity.UserEntity;
import at.technikum.springrestbackend.mapper.EventMapper;
import at.technikum.springrestbackend.repositories.EventRepository;
import at.technikum.springrestbackend.repositories.ImageRepository;
import at.technikum.springrestbackend.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final ImageRepository imageRepository;
    private final UserRepository userRepository;
    private final EventMapper eventMapper;

    public EventService(EventRepository eventRepository, ImageRepository imageRepository, UserRepository userRepository, EventMapper eventMapper) {
        this.eventRepository = eventRepository;
        this.imageRepository = imageRepository;
        this.userRepository = userRepository;
        this.eventMapper = eventMapper;
    }

    public List<EventDto> getEvents() {
        return eventRepository.findAll().stream().map(eventMapper::toDto).toList();
    }

    public EventDto createEvent(EventDto eventDto) {
        UserEntity host = userRepository.findById(eventDto.getHostId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        EventEntity eventEntity = eventMapper.toEntity(eventDto);
        eventEntity.setHost(host);

        if (eventDto.getImageId() != null) {
            ImageEntity image = imageRepository.findById(eventDto.getImageId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found"));
            eventEntity.setImage(image);
        }
        validateEventChronology(eventEntity);

        EventEntity savedEntity = eventRepository.save(eventEntity);
        return eventMapper.toDto(savedEntity);
    }

    private void validateEventDates(EventEntity event) {
        if (event.getEndDate().isBefore(event.getStartDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Event End Date cannot be before Start Date.");
        }
    }

    private void validateSalesDates(EventEntity event) {
        if (event.getSalesEnd().isBefore(event.getSalesStart())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sales End Date cannot be before Sales Start Date.");
        }
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

        validateEventChronology(savedEvent);

        if (eventDto.getImageId() != null) {
            ImageEntity image = imageRepository.findById(eventDto.getImageId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found"));
            savedEvent.setImage(image);
        }
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

    private void validateEventChronology(EventEntity event) {
        LocalDateTime salesStart = event.getSalesStart();
        LocalDateTime salesEnd = event.getSalesEnd();
        LocalDateTime eventStart = event.getStartDate();
        LocalDateTime eventEnd = event.getEndDate();

        if (salesStart != null && salesEnd != null && salesEnd.isBefore(salesStart)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sales End Date cannot be before Sales Start Date.");
        }

        if (eventStart != null && eventEnd != null && eventEnd.isBefore(eventStart)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Event End Date cannot be before Start Date.");
        }

        if (salesEnd != null && eventStart != null && salesEnd.isAfter(eventStart)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ticket sales must end before or when the event starts.");
        }

        if (salesStart != null && eventStart != null && salesStart.isAfter(eventStart)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ticket sales cannot start after the event has started.");
        }
    }
}
