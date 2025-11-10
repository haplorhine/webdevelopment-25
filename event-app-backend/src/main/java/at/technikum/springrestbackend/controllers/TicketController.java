package at.technikum.springrestbackend.controllers;

import at.technikum.springrestbackend.dto.TicketDto;
import at.technikum.springrestbackend.services.TicketService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping("/tickets")
    public List<TicketDto> getTickets() {
        return ticketService.getTickets();
    }

    @PostMapping("/tickets")
    public TicketDto createTicket(@RequestBody @Valid TicketDto ticketDto) {
        return ticketService.createTicket(ticketDto);
    }

    @GetMapping("/tickets/{id}")
    public TicketDto getTicketById(@PathVariable java.util.UUID id) {
        return ticketService.getTicketById(id);
    }

    @PutMapping("/tickets/{id}")
    public TicketDto updateTicket(@PathVariable java.util.UUID id, @RequestBody @Valid TicketDto ticketDto) {
        return ticketService.updateTicket(id, ticketDto);
    }

    @DeleteMapping("/tickets/{id}")
    public org.springframework.http.ResponseEntity<Void> deleteTicket(@PathVariable java.util.UUID id) {
        ticketService.deleteTicketById(id);
        return org.springframework.http.ResponseEntity.noContent().build();
    }
}
