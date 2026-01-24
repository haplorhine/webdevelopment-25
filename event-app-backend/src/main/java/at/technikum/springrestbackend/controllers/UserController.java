package at.technikum.springrestbackend.controllers;

import at.technikum.springrestbackend.dto.UserCreationDto;
import at.technikum.springrestbackend.dto.UserDto;
import at.technikum.springrestbackend.dto.TicketDto;
import at.technikum.springrestbackend.services.UserService;
import at.technikum.springrestbackend.services.TicketService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class UserController {
    private final UserService userService;
    private final TicketService ticketService;

    public UserController(UserService userService, TicketService ticketService) {
        this.userService = userService;
        this.ticketService = ticketService;
    }

    @GetMapping("/users")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<UserDto> getUsers() {
        return userService.getUsers();
    }

    @PostMapping("/users")
    public UserDto createUser(@RequestBody @Valid UserCreationDto userCreationDto) {
        return userService.createUser(userCreationDto);
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or hasPermission(#id, T(at.technikum.springrestbackend.entity.UserEntity).getName(), 'delete')")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or hasPermission(#id, T(at.technikum.springrestbackend.entity.UserEntity).getName(), 'read')")
    public UserDto getUserById(@PathVariable java.util.UUID id) {
        return userService.getUserById(id);
    }

    @PutMapping("/users/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or hasPermission(#id, T(at.technikum.springrestbackend.entity.UserEntity).getName(), 'write')")
    public UserDto updateUser(@PathVariable UUID id, @RequestBody @Valid UserCreationDto userCreationDto) {
        return userService.updateUser(id, userCreationDto);
    }

    @GetMapping("/users/{id}/tickets")
    @PreAuthorize("hasAuthority('ADMIN') or hasPermission(#id, T(at.technikum.springrestbackend.entity.UserEntity).getName(), 'read')")
    public List<TicketDto> getTicketsByUser(@PathVariable UUID id) {
        return ticketService.getTicketsByUserId(id);
    }
}
