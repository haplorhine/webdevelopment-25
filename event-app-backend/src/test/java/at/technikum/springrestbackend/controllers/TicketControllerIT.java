package at.technikum.springrestbackend.controllers;

import at.technikum.springrestbackend.dto.TicketDto;
import at.technikum.springrestbackend.entity.TicketStatus;
import at.technikum.springrestbackend.services.TicketService;
import at.technikum.springrestbackend.security.UserPrincipal;
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
import java.util.UUID;

import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TicketControllerIT {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Container
    @ServiceConnection
    static MariaDBContainer mariadb = new MariaDBContainer("mariadb:10.3.39");

    @MockitoBean
    private TicketService ticketService;

    @Test
    @WithMockUser(authorities = "ADMIN")
    void adminUser_getTickets_returnsTicketList() throws Exception {
        // given
        TicketDto ticket1 = new TicketDto();
        ticket1.setId(UUID.randomUUID());
        ticket1.setEventId(UUID.randomUUID());
        ticket1.setUserId(UUID.randomUUID());
        ticket1.setPurchaseDate(LocalDateTime.now());
        ticket1.setStatus(TicketStatus.ACTIVE);

        TicketDto ticket2 = new TicketDto();
        ticket2.setId(UUID.randomUUID());
        ticket2.setEventId(UUID.randomUUID());
        ticket2.setUserId(UUID.randomUUID());
        ticket2.setPurchaseDate(LocalDateTime.now());
        ticket2.setStatus(TicketStatus.CANCELLED);

        when(ticketService.getTickets()).thenReturn(java.util.Arrays.asList(ticket1, ticket2));

        // when
        ResultActions resultActions = mvc.perform(get("/tickets"));

        // then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$[0].id", notNullValue()));
        resultActions.andExpect(jsonPath("$[1].id", notNullValue()));
    }

    @Test
    @WithMockUser(roles = "USER")
    void regularUser_getTickets_returnsForbidden() throws Exception {
        // when
        ResultActions resultActions = mvc.perform(get("/tickets"));

        // then
        resultActions.andExpect(status().isForbidden());
    }

    @Test
    void unauthenticatedUser_getTickets_returnsForbidden() throws Exception {
        // when
        ResultActions resultActions = mvc.perform(get("/tickets"));

        // then
        resultActions.andExpect(status().isForbidden());
    }

    private RequestPostProcessor userPrincipal(UUID userId) {
        return SecurityMockMvcRequestPostProcessors.authentication(
            new UsernamePasswordAuthenticationToken(
                new UserPrincipal(userId, "testuser", "password", "USER", true),
                null,
                java.util.List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority("USER"))
            )
        );
    }

    @Test
    void authenticatedUser_createTicket_returnsCreatedTicket() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        TicketDto request = new TicketDto();
        request.setEventId(UUID.randomUUID());
        request.setUserId(userId);
        request.setPurchaseDate(LocalDateTime.now());
        request.setStatus(TicketStatus.ACTIVE);

        TicketDto response = new TicketDto();
        response.setId(UUID.randomUUID());
        response.setEventId(request.getEventId());
        response.setUserId(request.getUserId());
        response.setPurchaseDate(request.getPurchaseDate());
        response.setStatus(TicketStatus.ACTIVE);

        when(ticketService.createTicket(any(TicketDto.class))).thenReturn(response);

        // when
        ResultActions resultActions = mvc.perform(post("/tickets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(userPrincipal(userId)));

        // then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.id", notNullValue()));
    }

    @Test
    void unauthenticatedUser_createTicket_returnsForbidden() throws Exception {
        // given
        TicketDto request = new TicketDto();
        request.setEventId(UUID.randomUUID());
        request.setUserId(UUID.randomUUID());
        request.setPurchaseDate(LocalDateTime.now());

        // when
        ResultActions resultActions = mvc.perform(post("/tickets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        resultActions.andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void missingPurchaseDate_createTicket_returnsBadRequest() throws Exception {
        // given
        TicketDto request = new TicketDto();
        request.setEventId(UUID.randomUUID());
        request.setUserId(UUID.randomUUID());

        // when
        ResultActions resultActions = mvc.perform(post("/tickets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        resultActions.andExpect(status().isBadRequest());
    }


    @Test
    @WithMockUser(authorities = "ADMIN")
    void adminUser_getTicketById_returnsTicket() throws Exception {
        // given
        UUID ticketId = UUID.randomUUID();
        TicketDto ticket = new TicketDto();
        ticket.setId(ticketId);
        ticket.setEventId(UUID.randomUUID());
        ticket.setUserId(UUID.randomUUID());
        ticket.setPurchaseDate(LocalDateTime.now());
        ticket.setStatus(TicketStatus.ACTIVE);

        when(ticketService.getTicketById(ticketId)).thenReturn(ticket);

        // when
        ResultActions resultActions = mvc.perform(get("/tickets/" + ticketId));

        // then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.id", notNullValue()));
    }

    @Test
    void unauthenticatedUser_getTicketById_returnsForbidden() throws Exception {
        // given
        UUID ticketId = UUID.randomUUID();

        // when
        ResultActions resultActions = mvc.perform(get("/tickets/" + ticketId));

        // then
        resultActions.andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void adminUser_updateTicket_returnsUpdatedTicket() throws Exception {
        // given
        UUID ticketId = UUID.randomUUID();
        TicketDto request = new TicketDto();
        request.setEventId(UUID.randomUUID());
        request.setUserId(UUID.randomUUID());
        request.setPurchaseDate(LocalDateTime.now());
        request.setStatus(TicketStatus.CANCELLED);

        TicketDto response = new TicketDto();
        response.setId(ticketId);
        response.setEventId(request.getEventId());
        response.setUserId(request.getUserId());
        response.setPurchaseDate(request.getPurchaseDate());
        response.setStatus(TicketStatus.CANCELLED);

        when(ticketService.updateTicket(any(UUID.class), any(TicketDto.class))).thenReturn(response);

        // when
        ResultActions resultActions = mvc.perform(put("/tickets/" + ticketId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void regularUser_updateTicket_returnsForbidden() throws Exception {
        // given
        UUID ticketId = UUID.randomUUID();
        TicketDto request = new TicketDto();
        request.setEventId(UUID.randomUUID());
        request.setUserId(UUID.randomUUID());
        request.setPurchaseDate(LocalDateTime.now());

        // when
        ResultActions resultActions = mvc.perform(put("/tickets/" + ticketId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        resultActions.andExpect(status().isForbidden());
    }

    @Test
    void unauthenticatedUser_updateTicket_returnsForbidden() throws Exception {
        // given
        UUID ticketId = UUID.randomUUID();
        TicketDto request = new TicketDto();
        request.setEventId(UUID.randomUUID());
        request.setUserId(UUID.randomUUID());
        request.setPurchaseDate(LocalDateTime.now());

        // when
        ResultActions resultActions = mvc.perform(put("/tickets/" + ticketId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        resultActions.andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void adminUser_deleteTicket_returnsNoContent() throws Exception {
        // given
        UUID ticketId = UUID.randomUUID();

        // when
        ResultActions resultActions = mvc.perform(delete("/tickets/" + ticketId));

        // then
        resultActions.andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "USER")
    void regularUser_deleteTicket_returnsForbidden() throws Exception {
        // given
        UUID ticketId = UUID.randomUUID();

        // when
        ResultActions resultActions = mvc.perform(delete("/tickets/" + ticketId));

        // then
        resultActions.andExpect(status().isForbidden());
    }

    @Test
    void unauthenticatedUser_deleteTicket_returnsForbidden() throws Exception {
        // given
        UUID ticketId = UUID.randomUUID();

        // when
        ResultActions resultActions = mvc.perform(delete("/tickets/" + ticketId));

        // then
        resultActions.andExpect(status().isForbidden());
    }
}
