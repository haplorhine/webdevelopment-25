package at.technikum.springrestbackend.controllers;

import at.technikum.springrestbackend.dto.UserDto;
import at.technikum.springrestbackend.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public List<UserDto> getUsers() {
        return userService.getUsers();
    }

    @PostMapping("/users")
    public UserDto createUser(@RequestBody @Valid UserDto userDto) {
        return userService.createUser(userDto);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users/{id}")
    @PreAuthorize("hasPermission(#id, T(at.technikum.springrestbackend.entity.UserEntity).getName(),'read')")
    public UserDto getUserById(@PathVariable java.util.UUID id) {
        return userService.getUserById(id);
    }

    @PutMapping("/users/{id}")
    @PreAuthorize("hasPermission(#id, T(at.technikum.springrestbackend.entity.UserEntity).getName(),'write')")
    public UserDto updateUser(@PathVariable UUID id, @RequestBody @Valid UserDto userDto) {
        return userService.updateUser(id, userDto);
    }
}
