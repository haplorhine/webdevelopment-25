package at.technikum.springrestbackend.controllers;

import at.technikum.springrestbackend.dto.EventDto;
import at.technikum.springrestbackend.services.EventService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class EventController {
    private EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/events")
    public List<EventDto> getEvents() {
        return eventService.getEvents();
    }

    @PostMapping("/events")
    public EventDto createEvent(@RequestBody @Valid EventDto eventDto) {
        return eventService.createEvent(eventDto);
    }

    @DeleteMapping("/events/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable UUID id) {
        eventService.deleteEventById(id);
        return ResponseEntity.noContent().build();
    }
}
