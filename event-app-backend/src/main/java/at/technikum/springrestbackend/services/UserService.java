package at.technikum.springrestbackend.services;

import at.technikum.springrestbackend.dto.UserDto;
import at.technikum.springrestbackend.entity.UserEntity;
import at.technikum.springrestbackend.mapper.UserMapper;
import at.technikum.springrestbackend.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public List<UserDto> getUsers() {
        return userRepository.findAll().stream().map(userMapper::toDto).toList();
    }

    public UserDto createUser(UserDto userDto) {
        UserEntity userEntity = userMapper.toEntity(userDto);
        UserEntity savedUser = userRepository.save(userEntity);
        return userMapper.toDto(savedUser);
    }

    public void deleteUserById(UUID id) {
        userRepository.deleteById(id);
    }

    public UserDto getUserById(UUID id) {
        Optional<UserEntity> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return userMapper.toDto(userOpt.get());
    }

    public UserDto updateUser(UUID id, UserDto userDto) {
        UserEntity savedUser = userRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        userMapper.updateEntityFromDto(userDto, savedUser);
        UserEntity updatedUser = userRepository.save(savedUser);
        return userMapper.toDto(updatedUser);
    }
}
