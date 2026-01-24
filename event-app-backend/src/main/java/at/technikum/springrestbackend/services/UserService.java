package at.technikum.springrestbackend.services;

import at.technikum.springrestbackend.dto.UserCreationDto;
import at.technikum.springrestbackend.dto.UserDto;
import at.technikum.springrestbackend.entity.ImageEntity;
import at.technikum.springrestbackend.entity.UserEntity;
import at.technikum.springrestbackend.entity.UserType;
import at.technikum.springrestbackend.mapper.UserMapper;
import at.technikum.springrestbackend.repositories.ImageRepository;
import at.technikum.springrestbackend.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final ImageRepository imageRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, ImageRepository imageRepository, UserMapper userMapper,  PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.imageRepository = imageRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserDto> getUsers() {
        return userRepository.findAll().stream().map(userMapper::toDto).toList();
    }

    public UserDto createUser(UserCreationDto userCreationDto) {

        if (UserType.ADMIN.equals(userCreationDto.getUserType())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Registration as ADMIN is not allowed.");
        }
        userCreationDto.setPassword(passwordEncoder.encode(userCreationDto.getPassword()));
        UserEntity userEntity = userMapper.toEntity(userCreationDto);

        userEntity.setActive(true);

        if (userCreationDto.getImageId() != null) {
            ImageEntity image = imageRepository.findById(userCreationDto.getImageId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found"));
            userEntity.setImage(image);
        }

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

//    public UserEntity findUserByUsername(String username) {
//        Optional<UserEntity> userOpt = userRepository.findByUsername(username);
//        if (userOpt.isEmpty()) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
//        }
//        return userOpt.get();
//    }

    public UserDto updateUser(UUID id, UserCreationDto dto) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // username
        if (dto.getUsername() != null && !dto.getUsername().isBlank()) {
            Optional<UserEntity> existing = userRepository.findByUsername(dto.getUsername());
            if (existing.isPresent() && !existing.get().getId().equals(id)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already taken");
            }
            userEntity.setUsername(dto.getUsername());
        }

        // email
        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            Optional<UserEntity> existing = userRepository.findByEmail(dto.getEmail());
            if (existing.isPresent() && !existing.get().getId().equals(id)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already taken");
            }
            userEntity.setEmail(dto.getEmail());
        }

        // pw
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            userEntity.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        // salutation & country
        if (dto.getSalutation() != null) {
            userEntity.setSalutation(dto.getSalutation());
        }
        if (dto.getCountry() != null) {
            userEntity.setCountry(dto.getCountry());
        }

        // image
        if (dto.getImageId() != null) {
            ImageEntity image = imageRepository.findById(dto.getImageId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found"));
            userEntity.setImage(image);
        }

        // active
        if(dto.getIsActive() != null){
            userEntity.setActive(dto.getIsActive());
        }

        UserEntity updatedUser = userRepository.save(userEntity);
        return userMapper.toDto(updatedUser);
    }
}
