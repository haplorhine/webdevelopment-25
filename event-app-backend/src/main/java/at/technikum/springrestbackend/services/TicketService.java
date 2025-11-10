package at.technikum.springrestbackend.services;

import at.technikum.springrestbackend.dto.TicketDto;
import at.technikum.springrestbackend.entity.TicketEntity;
import at.technikum.springrestbackend.mapper.TicketMapper;

import at.technikum.springrestbackend.repositories.TicketRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    public TicketDto getTicketById(UUID id) {
        Optional<TicketEntity> eventOpt = ticketRepository.findById(id);
        if (eventOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return ticketMapper.toDto(eventOpt.get());
    }

    public TicketDto updateTicket(UUID id, TicketDto ticketDto) {
        TicketEntity savedTicket = ticketRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        ticketMapper.updateEntityFromDto(ticketDto, savedTicket);
        TicketEntity updatedTicket = ticketRepository.save(savedTicket);
        return ticketMapper.toDto(updatedTicket);
    }

    public void deleteTicketById(UUID id) {
        ticketRepository.deleteById(id);
    }
}
