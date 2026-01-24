package at.technikum.springrestbackend.controllers;

import at.technikum.springrestbackend.dto.EventDto;
import at.technikum.springrestbackend.entity.Category;
import at.technikum.springrestbackend.services.EventService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mariadb.MariaDBContainer;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@ActiveProfiles("test")
class EventControllerIT {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Container
    @ServiceConnection
    static MariaDBContainer mariadb = new MariaDBContainer("mariadb:10.3.39");

    @MockitoBean
    private EventService eventService;

    @Test
    @WithMockUser(roles = "USER")
    void authenticatedUser_getEvents_returnsEventList() throws Exception {
        // given
        EventDto event1 = new EventDto();
        event1.setId(UUID.randomUUID());
        event1.setTitle("Test Event 1");
        event1.setLocation("Vienna");

        EventDto event2 = new EventDto();
        event2.setId(UUID.randomUUID());
        event2.setTitle("Test Event 2");
        event2.setLocation("Salzburg");

        List<EventDto> events = Arrays.asList(event1, event2);
        when(eventService.getEvents()).thenReturn(events);

        // when
        ResultActions resultActions = mvc.perform(get("/events"));

        // then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @WithMockUser(authorities = "HOST")
    void hostUser_createEvent_returnsCreatedEvent() throws Exception {
        // given
        LocalDateTime now = LocalDateTime.now();
        EventDto request = new EventDto();
        request.setTitle("New Event");
        request.setCategory(Category.CONCERT);
        request.setLocation("Vienna");
        request.setStartDate(now.plusDays(10));
        request.setEndDate(now.plusDays(11));
        request.setMaxParticipants(100);
        request.setSalesStart(now.plusDays(1));
        request.setSalesEnd(now.plusDays(9));
        request.setTicketPrice(25.0);
        request.setHostId(UUID.randomUUID());

        EventDto response = new EventDto();
        response.setId(UUID.randomUUID());
        response.setTitle("New Event");
        response.setLocation("Vienna");

        when(eventService.createEvent(any(EventDto.class))).thenReturn(response);

        // when
        ResultActions resultActions = mvc.perform(post("/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.id", notNullValue()));
    }

    @Test
    @WithMockUser(roles = "USER")
    void regularUser_createEvent_returnsForbidden() throws Exception {
        // given
        LocalDateTime now = LocalDateTime.now();
        EventDto request = new EventDto();
        request.setTitle("New Event");
        request.setLocation("Vienna");
        request.setStartDate(now.plusDays(10));
        request.setEndDate(now.plusDays(11));
        request.setMaxParticipants(100);
        request.setSalesStart(now.plusDays(1));
        request.setSalesEnd(now.plusDays(9));
        request.setHostId(UUID.randomUUID());

        // when
        ResultActions resultActions = mvc.perform(post("/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        resultActions.andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void authenticatedUser_getEventById_returnsEvent() throws Exception {
        // given
        UUID eventId = UUID.randomUUID();
        EventDto event = new EventDto();
        event.setId(eventId);
        event.setTitle("Test Event");
        event.setLocation("Vienna");

        when(eventService.getEventById(eq(eventId))).thenReturn(event);

        // when
        ResultActions resultActions = mvc.perform(get("/events/" + eventId));

        // then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.id", notNullValue()));
    }


    @Test
    @WithMockUser(authorities = "HOST")
    void invalidEventData_createEvent_returnsBadRequest() throws Exception {
        // given
        EventDto request = new EventDto();
        request.setTitle("A"); // too short
        request.setLocation("Vienna");

        // when
        ResultActions resultActions = mvc.perform(post("/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "HOST")
    void missingRequiredFields_createEvent_returnsBadRequest() throws Exception {
        // given
        EventDto request = new EventDto();
        request.setTitle("Valid Title");
        // missing required fields

        // when
        ResultActions resultActions = mvc.perform(post("/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        resultActions.andExpect(status().isBadRequest());
    }
}
