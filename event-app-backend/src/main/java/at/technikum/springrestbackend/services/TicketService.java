package at.technikum.springrestbackend.services;

import at.technikum.springrestbackend.dto.TicketDto;
import at.technikum.springrestbackend.entity.EventEntity;
import at.technikum.springrestbackend.entity.TicketEntity;
import at.technikum.springrestbackend.entity.TicketStatus;
import at.technikum.springrestbackend.entity.UserEntity;
import at.technikum.springrestbackend.mapper.TicketMapper;

import at.technikum.springrestbackend.repositories.EventRepository;
import at.technikum.springrestbackend.repositories.TicketRepository;

import at.technikum.springrestbackend.repositories.UserRepository;
import at.technikum.springrestbackend.security.UserPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final TicketMapper ticketMapper;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public TicketService(TicketRepository ticketRepository, TicketMapper ticketMapper, EventRepository eventRepository,
                         UserRepository userRepository) {
        this.ticketRepository = ticketRepository;
        this.ticketMapper = ticketMapper;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    public List<TicketDto> getTickets() {
        return ticketRepository.findAll().stream().map(ticketMapper::toDto).toList();
    }

    public TicketDto createTicket(TicketDto ticketDto) {
        EventEntity event = eventRepository.findById(ticketDto.getEventId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));

        UserEntity user = userRepository.findById(ticketDto.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));


        validateAvailability(event);
        validateActiveSales(event);

        TicketEntity ticketEntity = ticketMapper.toEntity(ticketDto);
        ticketEntity.setEvent(event);
        ticketEntity.setUser(user);

        if (ticketEntity.getStatus() == null) {
            ticketEntity.setStatus(TicketStatus.ACTIVE);
        }

        TicketEntity savedEntity = ticketRepository.save(ticketEntity);
        return ticketMapper.toDto(savedEntity);
    }

    private void validateAvailability(EventEntity event) {
        int currentTicketCount = event.getTickets() != null ? event.getTickets().size() : 0;
        if (currentTicketCount >= event.getMaxParticipants()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Event is sold out.");
        }
    }

    private void validateActiveSales(EventEntity event) {
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(event.getSalesStart()) || now.isAfter(event.getSalesEnd())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ticket sales are currently not active.");
        }
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

    public List<TicketDto> getTicketsByUserId(UUID userId) {
        return ticketRepository.findAllByUserId(userId).stream().map(ticketMapper::toDto).toList();
    }
}
