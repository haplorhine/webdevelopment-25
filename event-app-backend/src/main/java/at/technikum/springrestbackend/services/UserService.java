package at.technikum.springrestbackend.services;

import at.technikum.springrestbackend.dto.UserDto;
import at.technikum.springrestbackend.entity.UserEntity;
import at.technikum.springrestbackend.mapper.UserMapper;
import at.technikum.springrestbackend.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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
}

