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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private TicketMapper ticketMapper;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TicketService ticketService;

    private UUID ticketId;
    private UUID eventId;
    private UUID userId;
    private EventEntity event;
    private UserEntity user;
    private TicketDto ticketDto;
    private TicketEntity ticketEntity;

    @BeforeEach
    void setUp() {
        ticketId = UUID.randomUUID();
        eventId = UUID.randomUUID();
        userId = UUID.randomUUID();
        user = TestDataFactory.createUser(userId);
        event = TestDataFactory.createEvent(eventId, user);
        ticketDto = TestDataFactory.createTicketDto(eventId, userId);
        ticketEntity = TestDataFactory.createTicket(ticketId, event, user);
        ticketEntity.setStatus(null);
        event.setTickets(new ArrayList<>());
    }

    @Test
    void getTickets_returnsMappedDtos() {
        TicketEntity other = TestDataFactory.createTicket(UUID.randomUUID(), event, user);
        TicketDto otherDto = TestDataFactory.createTicketDto(eventId, userId);
        when(ticketRepository.findAll()).thenReturn(List.of(ticketEntity, other));
        when(ticketMapper.toDto(ticketEntity)).thenReturn(ticketDto);
        when(ticketMapper.toDto(other)).thenReturn(otherDto);

        List<TicketDto> result = ticketService.getAllTickets();

        assertThat(result).containsExactly(ticketDto, otherDto);
        verify(ticketRepository).findAll();
        verify(ticketMapper, times(2)).toDto(any(TicketEntity.class));
    }

    @Test
    void createTicket_whenEventMissing_throwsNotFound() {
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ticketService.createTicket(ticketDto))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void createTicket_whenUserMissing_throwsNotFound() {
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ticketService.createTicket(ticketDto))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void createTicket_whenEventDatesInvalid_throwsBadRequest() {
        event.setEndDate(event.getStartDate().minusDays(1));
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> ticketService.createTicket(ticketDto))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Event End Date")
                .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void createTicket_whenSalesDatesInvalid_throwsBadRequest() {
        event.setSalesEnd(event.getSalesStart().minusDays(1));
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> ticketService.createTicket(ticketDto))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Sales End Date")
                .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void createTicket_whenEventSoldOut_throwsBadRequest() {
        event.setMaxParticipants(1);
        event.setTickets(List.of(TestDataFactory.createTicket(UUID.randomUUID(), event, user)));
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> ticketService.createTicket(ticketDto))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("sold out")
                .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void createTicket_whenSalesInactive_throwsBadRequest() {
        LocalDateTime now = LocalDateTime.now();
        event.setSalesStart(now.plusDays(1));
        event.setSalesEnd(now.plusDays(2));
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> ticketService.createTicket(ticketDto))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("not active")
                .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void createTicket_successfullyCreatesAndDefaultsStatus() {
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(ticketMapper.toEntity(ticketDto)).thenReturn(ticketEntity);
        when(ticketRepository.save(ticketEntity)).thenReturn(ticketEntity);
        when(ticketMapper.toDto(ticketEntity)).thenReturn(ticketDto);

        TicketDto result = ticketService.createTicket(ticketDto);

        assertThat(result).isEqualTo(ticketDto);
        assertThat(ticketEntity.getEvent()).isEqualTo(event);
        assertThat(ticketEntity.getUser()).isEqualTo(user);
        assertThat(ticketEntity.getStatus()).isEqualTo(TicketStatus.ACTIVE);
        verify(ticketRepository).save(ticketEntity);
    }

    @Test
    void getTicketById_whenMissing_throwsNotFound() {
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ticketService.getTicketById(ticketId))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void getTicketById_returnsDto() {
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticketEntity));
        when(ticketMapper.toDto(ticketEntity)).thenReturn(ticketDto);

        TicketDto result = ticketService.getTicketById(ticketId);

        assertThat(result).isEqualTo(ticketDto);
        verify(ticketRepository).findById(ticketId);
    }

    @Test
    void updateTicket_whenMissing_throwsNotFound() {
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ticketService.updateTicket(ticketId, ticketDto))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void updateTicket_updatesEntityAndReturnsDto() {
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticketEntity));
        when(ticketRepository.save(ticketEntity)).thenReturn(ticketEntity);
        when(ticketMapper.toDto(ticketEntity)).thenReturn(ticketDto);

        TicketDto result = ticketService.updateTicket(ticketId, ticketDto);

        assertThat(result).isEqualTo(ticketDto);
        verify(ticketMapper).updateEntityFromDto(ticketDto, ticketEntity);
        verify(ticketRepository).save(ticketEntity);
    }

    @Test
    void deleteTicketById_delegatesToRepository() {
        ticketService.deleteTicketById(ticketId);
        verify(ticketRepository).deleteById(ticketId);
    }
}
