package at.technikum.springrestbackend.controllers;

import at.technikum.springrestbackend.dto.TicketDto;
import at.technikum.springrestbackend.services.TicketService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
}
