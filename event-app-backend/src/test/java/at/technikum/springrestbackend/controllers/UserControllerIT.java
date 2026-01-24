package at.technikum.springrestbackend.controllers;

import at.technikum.springrestbackend.dto.UserCreationDto;
import at.technikum.springrestbackend.dto.UserDto;
import at.technikum.springrestbackend.entity.Country;
import at.technikum.springrestbackend.entity.Salutation;
import at.technikum.springrestbackend.entity.UserType;
import at.technikum.springrestbackend.services.UserService;
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

import java.util.UUID;

import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerIT {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Container
    @ServiceConnection
    static MariaDBContainer mariadb = new MariaDBContainer("mariadb:10.3.39");

    @MockitoBean
    private UserService userService;

    @Test
    @WithMockUser(authorities = "ADMIN")
    void adminUser_getUsers_returnsUserList() throws Exception {
        UserDto user1 = new UserDto();
        user1.setId(UUID.randomUUID());
        user1.setUsername("user1");
        user1.setEmail("user1@test.com");

        UserDto user2 = new UserDto();
        user2.setId(UUID.randomUUID());
        user2.setUsername("user2");
        user2.setEmail("user2@test.com");

        when(userService.getUsers()).thenReturn(java.util.Arrays.asList(user1, user2));

        ResultActions resultActions = mvc.perform(get("/users"));

        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$[0].username").value("user1"));
        resultActions.andExpect(jsonPath("$[1].username").value("user2"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void regularUser_getUsers_returnsForbidden() throws Exception {
        ResultActions resultActions = mvc.perform(get("/users"));
        resultActions.andExpect(status().isForbidden());
    }

    @Test
    void unauthenticatedUser_getUsers_returnsForbidden() throws Exception {
        ResultActions resultActions = mvc.perform(get("/users"));
        resultActions.andExpect(status().isForbidden());
    }

    @Test
    void validUserCreation_createUser_returnsCreatedUser() throws Exception {
        UserCreationDto request = new UserCreationDto();
        request.setSalutation(Salutation.MR);
        request.setEmail("newuser@test.com");
        request.setUsername("newuser");
        request.setUserType(UserType.USER);
        request.setPassword("password123");
        request.setCountry(Country.AUSTRIA);
        request.setIsActive(true);

        UserDto response = new UserDto();
        response.setId(UUID.randomUUID());
        response.setUsername("newuser");
        response.setEmail("newuser@test.com");

        when(userService.createUser(any(UserCreationDto.class))).thenReturn(response);

        ResultActions resultActions = mvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.id", notNullValue()));
    }

    @Test
    void invalidEmail_createUser_returnsBadRequest() throws Exception {
        UserCreationDto request = new UserCreationDto();
        request.setSalutation(Salutation.MR);
        request.setEmail("invalid-email");
        request.setUsername("newuser");
        request.setUserType(UserType.USER);
        request.setPassword("password123");
        request.setCountry(Country.AUSTRIA);

        ResultActions resultActions = mvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void shortPassword_createUser_returnsBadRequest() throws Exception {
        UserCreationDto request = new UserCreationDto();
        request.setSalutation(Salutation.MR);
        request.setEmail("user@test.com");
        request.setUsername("user");
        request.setUserType(UserType.USER);
        request.setPassword("123");
        request.setCountry(Country.AUSTRIA);

        ResultActions resultActions = mvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void missingUsername_createUser_returnsOk() throws Exception {
        UserCreationDto request = new UserCreationDto();
        request.setSalutation(Salutation.MR);
        request.setEmail("test@test.com");
        request.setUserType(UserType.USER);
        request.setPassword("password123");
        request.setCountry(Country.AUSTRIA);

        UserDto response = new UserDto();
        response.setId(UUID.randomUUID());
        response.setEmail("test@test.com");

        when(userService.createUser(any(UserCreationDto.class))).thenReturn(response);

        ResultActions resultActions = mvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        resultActions.andExpect(status().isOk());
    }

    @Test
    void nullSalutation_createUser_returnsBadRequest() throws Exception {
        UserCreationDto request = new UserCreationDto();
        request.setSalutation(null);
        request.setEmail("test@test.com");
        request.setUsername("testuser");
        request.setUserType(UserType.USER);
        request.setPassword("password123");
        request.setCountry(Country.AUSTRIA);

        ResultActions resultActions = mvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void nullCountry_createUser_returnsBadRequest() throws Exception {
        UserCreationDto request = new UserCreationDto();
        request.setSalutation(Salutation.MR);
        request.setEmail("test@test.com");
        request.setUsername("testuser");
        request.setUserType(UserType.USER);
        request.setPassword("password123");
        request.setCountry(null);

        ResultActions resultActions = mvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void adminUser_getUserById_returnsUser() throws Exception {
        UUID userId = UUID.randomUUID();
        UserDto user = new UserDto();
        user.setId(userId);
        user.setUsername("testuser");
        user.setEmail("test@test.com");

        when(userService.getUserById(userId)).thenReturn(user);

        ResultActions resultActions = mvc.perform(get("/users/" + userId));

        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.username").value("testuser"));
    }


    @Test
    void unauthenticatedUser_getUserById_returnsForbidden() throws Exception {
        UUID userId = UUID.randomUUID();
        ResultActions resultActions = mvc.perform(get("/users/" + userId));
        resultActions.andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void adminUser_updateUser_returnsUpdatedUser() throws Exception {
        UUID userId = UUID.randomUUID();
        UserCreationDto request = new UserCreationDto();
        request.setSalutation(Salutation.MR);
        request.setEmail("updated@test.com");
        request.setUsername("updateduser");
        request.setUserType(UserType.USER);
        request.setPassword("password123");
        request.setCountry(Country.AUSTRIA);

        UserDto response = new UserDto();
        response.setId(userId);
        response.setUsername("updateduser");

        when(userService.updateUser(any(UUID.class), any(UserCreationDto.class))).thenReturn(response);

        ResultActions resultActions = mvc.perform(put("/users/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        resultActions.andExpect(status().isOk());
    }


    @Test
    @WithMockUser(authorities = "ADMIN")
    void adminUser_deleteUser_returnsNoContent() throws Exception {
        UUID userId = UUID.randomUUID();
        ResultActions resultActions = mvc.perform(delete("/users/" + userId));
        resultActions.andExpect(status().isNoContent());
    }


    @Test
    void unauthenticatedUser_deleteUser_returnsForbidden() throws Exception {
        UUID userId = UUID.randomUUID();
        ResultActions resultActions = mvc.perform(delete("/users/" + userId));
        resultActions.andExpect(status().isForbidden());
    }
}
