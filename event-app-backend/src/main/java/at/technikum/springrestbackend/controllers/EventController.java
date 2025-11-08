package at.technikum.springrestbackend.controllers;

import at.technikum.springrestbackend.dto.EventDto;
import at.technikum.springrestbackend.services.EventService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

}
