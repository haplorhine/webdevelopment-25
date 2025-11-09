package at.technikum.springrestbackend.services;

import at.technikum.springrestbackend.dto.TicketDto;
import at.technikum.springrestbackend.entity.TicketEntity;
import at.technikum.springrestbackend.mapper.TicketMapper;

import at.technikum.springrestbackend.repositories.TicketRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final TicketMapper ticketMapper;

    public TicketService(TicketRepository ticketRepository, TicketMapper ticketMapper) {
        this.ticketRepository = ticketRepository;
        this.ticketMapper = ticketMapper;
    }

    public List<TicketDto> getTickets() {
        return ticketRepository.findAll().stream().map(ticketMapper::toDto).toList();
    }

    public TicketDto createTicket(TicketDto ticketDto) {
        TicketEntity ticketEntity = ticketMapper.toEntity(ticketDto);
        TicketEntity savedEntity = ticketRepository.save(ticketEntity);
        return ticketMapper.toDto(savedEntity);
    }
}
