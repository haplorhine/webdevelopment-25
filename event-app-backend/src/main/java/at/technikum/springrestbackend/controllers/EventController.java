package at.technikum.springrestbackend.controllers;

import at.technikum.springrestbackend.dto.EventDto;
import at.technikum.springrestbackend.security.UserPrincipal;
import at.technikum.springrestbackend.services.EventService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

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
    @PreAuthorize("hasPermission(#eventDto.hostId, T(at.technikum.springrestbackend.entity.EventEntity).getName(), 'create')")
    public EventDto createEvent(@RequestBody @Valid EventDto eventDto) {
        return eventService.createEvent(eventDto);
    }

    @DeleteMapping("/events/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or hasPermission(#id, T(at.technikum.springrestbackend.entity.EventEntity).getName(), 'delete')")
    public ResponseEntity<Void> deleteEvent(@PathVariable UUID id) {
        eventService.deleteEventById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/events/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or hasPermission(#id, T(at.technikum.springrestbackend.entity.EventEntity).getName(), 'write')")
    public EventDto updateEvent(@PathVariable UUID id, @RequestBody @Valid EventDto eventDto) {
        return eventService.updateEvent(id, eventDto);
    }

    @GetMapping("/events/{id}")
    public EventDto getEventById(@PathVariable UUID id) {
        return eventService.getEventById(id);
    }
}
