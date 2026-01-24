package at.technikum.springrestbackend.controllers;

import at.technikum.springrestbackend.dto.TokenRequestDto;
import at.technikum.springrestbackend.dto.TokenResponseDto;
import at.technikum.springrestbackend.services.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mariadb.MariaDBContainer;

import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerIT {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Container
    @ServiceConnection
    static MariaDBContainer mariadb = new MariaDBContainer("mariadb:10.3.39");

    @MockitoBean
    private AuthService authService;

    @Test
    void validCredentials_authenticate_returnsToken() throws Exception {
        // given
        TokenRequestDto request = new TokenRequestDto();
        request.setUsername("testuser");
        request.setPassword("password123");

        TokenResponseDto response = TokenResponseDto.builder()
                .token("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test")
                .build();

        when(authService.authenticate(any(TokenRequestDto.class))).thenReturn(response);

        // when
        ResultActions resultActions = mvc.perform(post("/auth/token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.token", notNullValue()));
    }

    @Test
    void invalidCredentials_authenticate_returnsForbidden() throws Exception {
        // given
        TokenRequestDto request = new TokenRequestDto();
        request.setUsername("wronguser");
        request.setPassword("wrongpassword");

        when(authService.authenticate(any(TokenRequestDto.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // when
        ResultActions resultActions = mvc.perform(post("/auth/token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        resultActions.andExpect(status().isForbidden());
    }

    @Test
    void emptyUsername_authenticate_returnsBadRequest() throws Exception {
        // given
        TokenRequestDto request = new TokenRequestDto();
        request.setUsername("");
        request.setPassword("password123");

        // when
        ResultActions resultActions = mvc.perform(post("/auth/token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void emptyPassword_authenticate_returnsBadRequest() throws Exception {
        // given
        TokenRequestDto request = new TokenRequestDto();
        request.setUsername("testuser");
        request.setPassword("");

        // when
        ResultActions resultActions = mvc.perform(post("/auth/token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void nullUsername_authenticate_returnsBadRequest() throws Exception {
        // given
        TokenRequestDto request = new TokenRequestDto();
        request.setUsername(null);
        request.setPassword("password123");

        // when
        ResultActions resultActions = mvc.perform(post("/auth/token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void nullPassword_authenticate_returnsBadRequest() throws Exception {
        // given
        TokenRequestDto request = new TokenRequestDto();
        request.setUsername("testuser");
        request.setPassword(null);

        // when
        ResultActions resultActions = mvc.perform(post("/auth/token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void invalidJsonBody_authenticate_returnsBadRequest() throws Exception {
        // when
        ResultActions resultActions = mvc.perform(post("/auth/token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("invalid json content"));

        // then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void missingContentType_authenticate_returnsUnsupportedMediaType() throws Exception {
        // given
        TokenRequestDto request = new TokenRequestDto();
        request.setUsername("testuser");
        request.setPassword("password123");

        // when
        ResultActions resultActions = mvc.perform(post("/auth/token")
                .content(objectMapper.writeValueAsString(request)));

        // then
        resultActions.andExpect(status().isUnsupportedMediaType());
    }
}
