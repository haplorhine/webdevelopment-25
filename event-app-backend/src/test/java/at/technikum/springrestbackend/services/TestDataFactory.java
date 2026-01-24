package at.technikum.springrestbackend.services;

import at.technikum.springrestbackend.dto.EventDto;
import at.technikum.springrestbackend.dto.TicketDto;
import at.technikum.springrestbackend.entity.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

final class TestDataFactory {
    private TestDataFactory() {
    }

    static UserEntity createUser(UUID id) {
        UserEntity user = new UserEntity();
        user.setId(id);
        user.setUsername("user-" + id);
        user.setEmail("user-" + id + "@example.com");
        user.setSalutation(Salutation.MR);
        user.setCountry(Country.AUSTRIA);
        user.setUserType(UserType.USER);
        user.setPassword("password");
        user.setActive(true);
        return user;
    }

    static ImageEntity createImage(UUID id) {
        ImageEntity image = new ImageEntity();
        image.setId(id);
        image.setName("image-" + id);
        image.setExternalId("ext-" + id);
        image.setContentType("image/png");
        return image;
    }

    static EventEntity createEvent(UUID id, UserEntity host) {
        EventEntity event = new EventEntity();
        event.setId(id);
        event.setTitle("Sample Event");
        event.setCategory(Category.CONFERENCE);
        event.setLocation("Vienna");
        LocalDateTime now = LocalDateTime.now();
        event.setStartDate(now.plusDays(1));
        event.setEndDate(now.plusDays(2));
        event.setSalesStart(now.minusDays(2));
        event.setSalesEnd(now.plusDays(1));
        event.setMaxParticipants(100);
        event.setTicketPrice(15.0);
        event.setHost(host);
        event.setTickets(new ArrayList<>());
        return event;
    }

    static TicketEntity createTicket(UUID id, EventEntity event, UserEntity user) {
        TicketEntity ticket = new TicketEntity();
        ticket.setId(id);
        ticket.setEvent(event);
        ticket.setUser(user);
        ticket.setStatus(TicketStatus.ACTIVE);
        return ticket;
    }

    static EventDto createEventDto(UUID hostId, UUID imageId) {
        EventDto dto = new EventDto();
        dto.setHostId(hostId);
        dto.setImageId(imageId);
        dto.setTitle("Sample Event DTO");
        dto.setLocation("Vienna");
        LocalDateTime now = LocalDateTime.now();
        dto.setStartDate(now.plusDays(1));
        dto.setEndDate(now.plusDays(2));
        dto.setSalesStart(now.minusDays(2));
        dto.setSalesEnd(now.plusDays(1));
        dto.setMaxParticipants(50);
        dto.setTicketPrice(10.0);
        return dto;
    }

    static TicketDto createTicketDto(UUID eventId, UUID userId) {
        TicketDto dto = new TicketDto();
        dto.setEventId(eventId);
        dto.setUserId(userId);
        dto.setPurchaseDate(LocalDateTime.now());
        return dto;
    }
}
