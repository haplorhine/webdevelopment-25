package at.technikum.springrestbackend.services;

import at.technikum.springrestbackend.dto.UserCreationDto;
import at.technikum.springrestbackend.dto.UserDto;
import at.technikum.springrestbackend.entity.*;
import at.technikum.springrestbackend.mapper.UserMapper;
import at.technikum.springrestbackend.repositories.ImageRepository;
import at.technikum.springrestbackend.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ImageRepository imageRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UserEntity testUserEntity;
    private UserDto testUserDto;
    private UserCreationDto testUserCreationDto;
    private ImageEntity testImageEntity;
    private UUID testUserId;
    private UUID testImageId;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        testImageId = UUID.randomUUID();

        testUserEntity = new UserEntity();
        testUserEntity.setId(testUserId);
        testUserEntity.setUsername("testuser");
        testUserEntity.setEmail("test@example.com");
        testUserEntity.setPassword("encodedPassword");
        testUserEntity.setSalutation(Salutation.MR);
        testUserEntity.setCountry(Country.AUSTRIA);
        testUserEntity.setUserType(UserType.USER);

        testUserDto = new UserDto();
        testUserDto.setId(testUserId);
        testUserDto.setUsername("testuser");
        testUserDto.setEmail("test@example.com");
        testUserDto.setSalutation(Salutation.MR);
        testUserDto.setCountry(Country.AUSTRIA);
        testUserDto.setUserType(UserType.USER);

        testUserCreationDto = new UserCreationDto();
        testUserCreationDto.setUsername("newuser");
        testUserCreationDto.setEmail("newuser@example.com");
        testUserCreationDto.setPassword("plainPassword");
        testUserCreationDto.setSalutation(Salutation.MS);
        testUserCreationDto.setCountry(Country.GERMANY);
        testUserCreationDto.setUserType(UserType.USER);

        testImageEntity = new ImageEntity();
        testImageEntity.setId(testImageId);
    }

    @Test
    void getUsers_shouldReturnAllUsers() {
        UserEntity user2 = new UserEntity();
        user2.setId(UUID.randomUUID());
        user2.setUsername("user2");

        UserDto userDto2 = new UserDto();
        userDto2.setId(user2.getId());
        userDto2.setUsername("user2");

        List<UserEntity> userEntities = Arrays.asList(testUserEntity, user2);

        when(userRepository.findAll()).thenReturn(userEntities);
        when(userMapper.toDto(testUserEntity)).thenReturn(testUserDto);
        when(userMapper.toDto(user2)).thenReturn(userDto2);

        List<UserDto> result = userService.getUsers();

        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(testUserDto, userDto2);
        verify(userRepository).findAll();
        verify(userMapper, times(2)).toDto(any(UserEntity.class));
    }

    @Test
    void createUser_withValidData_shouldCreateUser() {
        when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");
        when(userMapper.toEntity(testUserCreationDto)).thenReturn(testUserEntity);
        when(userRepository.save(testUserEntity)).thenReturn(testUserEntity);
        when(userMapper.toDto(testUserEntity)).thenReturn(testUserDto);

        UserDto result = userService.createUser(testUserCreationDto);

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("testuser");
        verify(passwordEncoder).encode("plainPassword");
        verify(userRepository).save(testUserEntity);
    }

    @Test
    void createUser_withImageId_shouldCreateUserWithImage() {
        testUserCreationDto.setImageId(testImageId);
        when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");
        when(userMapper.toEntity(testUserCreationDto)).thenReturn(testUserEntity);
        when(imageRepository.findById(testImageId)).thenReturn(Optional.of(testImageEntity));
        when(userRepository.save(testUserEntity)).thenReturn(testUserEntity);
        when(userMapper.toDto(testUserEntity)).thenReturn(testUserDto);

        UserDto result = userService.createUser(testUserCreationDto);

        assertThat(result).isNotNull();
        verify(imageRepository).findById(testImageId);
        verify(userRepository).save(testUserEntity);
        assertThat(testUserEntity.getImage()).isEqualTo(testImageEntity);
    }

    @Test
    void createUser_withInvalidImageId_shouldThrowException() {
        testUserCreationDto.setImageId(testImageId);
        when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");
        when(userMapper.toEntity(testUserCreationDto)).thenReturn(testUserEntity);
        when(imageRepository.findById(testImageId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.createUser(testUserCreationDto))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Image not found")
                .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);

        verify(userRepository, never()).save(any());
    }

    @Test
    void createUser_asAdmin_shouldThrowForbidden() {
        testUserCreationDto.setUserType(UserType.ADMIN);

        assertThatThrownBy(() -> userService.createUser(testUserCreationDto))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Registration as ADMIN is not allowed")
                .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
                .isEqualTo(HttpStatus.FORBIDDEN);

        verify(userRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void getUserById_withExistingId_shouldReturnUser() {
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUserEntity));
        when(userMapper.toDto(testUserEntity)).thenReturn(testUserDto);

        UserDto result = userService.getUserById(testUserId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(testUserId);
        verify(userRepository).findById(testUserId);
    }

    @Test
    void getUserById_withNonExistingId_shouldThrowException() {
        when(userRepository.findById(testUserId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(testUserId))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void deleteUserById_shouldCallRepository() {
        UUID userId = UUID.randomUUID();

        userService.deleteUserById(userId);

        verify(userRepository).deleteById(userId);
    }

    @Test
    void updateUser_withUsername_shouldUpdateUsername() {
        testUserCreationDto.setUsername("updateduser");
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUserEntity));
        when(userRepository.findByUsername("updateduser")).thenReturn(Optional.empty());
        when(userRepository.save(testUserEntity)).thenReturn(testUserEntity);
        when(userMapper.toDto(testUserEntity)).thenReturn(testUserDto);

        UserDto result = userService.updateUser(testUserId, testUserCreationDto);

        assertThat(result).isNotNull();
        verify(userRepository).save(testUserEntity);
        assertThat(testUserEntity.getUsername()).isEqualTo("updateduser");
    }

    @Test
    void updateUser_withDuplicateUsername_shouldThrowConflict() {
        testUserCreationDto.setUsername("existinguser");
        UserEntity otherUser = new UserEntity();
        otherUser.setId(UUID.randomUUID());
        otherUser.setUsername("existinguser");

        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUserEntity));
        when(userRepository.findByUsername("existinguser")).thenReturn(Optional.of(otherUser));

        assertThatThrownBy(() -> userService.updateUser(testUserId, testUserCreationDto))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Username already taken")
                .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
                .isEqualTo(HttpStatus.CONFLICT);

        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_withEmail_shouldUpdateEmail() {
        testUserCreationDto.setUsername(null);
        testUserCreationDto.setEmail("newemail@example.com");
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUserEntity));
        when(userRepository.findByEmail("newemail@example.com")).thenReturn(Optional.empty());
        when(userRepository.save(testUserEntity)).thenReturn(testUserEntity);
        when(userMapper.toDto(testUserEntity)).thenReturn(testUserDto);

        UserDto result = userService.updateUser(testUserId, testUserCreationDto);

        assertThat(result).isNotNull();
        verify(userRepository).save(testUserEntity);
        assertThat(testUserEntity.getEmail()).isEqualTo("newemail@example.com");
    }

    @Test
    void updateUser_withDuplicateEmail_shouldThrowConflict() {
        testUserCreationDto.setUsername(null);
        testUserCreationDto.setEmail("existing@example.com");
        UserEntity otherUser = new UserEntity();
        otherUser.setId(UUID.randomUUID());
        otherUser.setEmail("existing@example.com");

        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUserEntity));
        when(userRepository.findByEmail("existing@example.com")).thenReturn(Optional.of(otherUser));

        assertThatThrownBy(() -> userService.updateUser(testUserId, testUserCreationDto))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Email already taken")
                .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
                .isEqualTo(HttpStatus.CONFLICT);

        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_withPassword_shouldEncodeAndUpdatePassword() {
        testUserCreationDto.setUsername(null);
        testUserCreationDto.setEmail(null);
        testUserCreationDto.setPassword("newPlainPassword");
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUserEntity));
        when(passwordEncoder.encode("newPlainPassword")).thenReturn("newEncodedPassword");
        when(userRepository.save(testUserEntity)).thenReturn(testUserEntity);
        when(userMapper.toDto(testUserEntity)).thenReturn(testUserDto);

        UserDto result = userService.updateUser(testUserId, testUserCreationDto);

        assertThat(result).isNotNull();
        verify(passwordEncoder).encode("newPlainPassword");
        verify(userRepository).save(testUserEntity);
        assertThat(testUserEntity.getPassword()).isEqualTo("newEncodedPassword");
    }

    @Test
    void updateUser_withSalutationAndCountry_shouldUpdateFields() {
        testUserCreationDto.setUsername(null);
        testUserCreationDto.setEmail(null);
        testUserCreationDto.setPassword(null);
        testUserCreationDto.setSalutation(Salutation.OTHER);
        testUserCreationDto.setCountry(Country.SWITZERLAND);

        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUserEntity));
        when(userRepository.save(testUserEntity)).thenReturn(testUserEntity);
        when(userMapper.toDto(testUserEntity)).thenReturn(testUserDto);

        UserDto result = userService.updateUser(testUserId, testUserCreationDto);

        assertThat(result).isNotNull();
        verify(userRepository).save(testUserEntity);
        assertThat(testUserEntity.getSalutation()).isEqualTo(Salutation.OTHER);
        assertThat(testUserEntity.getCountry()).isEqualTo(Country.SWITZERLAND);
    }

    @Test
    void updateUser_withImageId_shouldUpdateImage() {
        testUserCreationDto.setUsername(null);
        testUserCreationDto.setEmail(null);
        testUserCreationDto.setPassword(null);
        testUserCreationDto.setImageId(testImageId);

        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUserEntity));
        when(imageRepository.findById(testImageId)).thenReturn(Optional.of(testImageEntity));
        when(userRepository.save(testUserEntity)).thenReturn(testUserEntity);
        when(userMapper.toDto(testUserEntity)).thenReturn(testUserDto);

        UserDto result = userService.updateUser(testUserId, testUserCreationDto);

        assertThat(result).isNotNull();
        verify(imageRepository).findById(testImageId);
        verify(userRepository).save(testUserEntity);
        assertThat(testUserEntity.getImage()).isEqualTo(testImageEntity);
    }

    @Test
    void updateUser_withInvalidImageId_shouldThrowException() {
        testUserCreationDto.setUsername(null);
        testUserCreationDto.setEmail(null);
        testUserCreationDto.setPassword(null);
        testUserCreationDto.setImageId(testImageId);

        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUserEntity));
        when(imageRepository.findById(testImageId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUser(testUserId, testUserCreationDto))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Image not found")
                .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);

        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_withNonExistingUserId_shouldThrowException() {
        when(userRepository.findById(testUserId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUser(testUserId, testUserCreationDto))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("User not found")
                .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);

        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_withBlankUsername_shouldNotUpdateUsername() {
        String originalUsername = testUserEntity.getUsername();
        testUserCreationDto.setUsername("   ");
        testUserCreationDto.setEmail(null);
        testUserCreationDto.setPassword(null);

        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUserEntity));
        when(userRepository.save(testUserEntity)).thenReturn(testUserEntity);
        when(userMapper.toDto(testUserEntity)).thenReturn(testUserDto);

        UserDto result = userService.updateUser(testUserId, testUserCreationDto);

        assertThat(result).isNotNull();
        verify(userRepository).save(testUserEntity);
        assertThat(testUserEntity.getUsername()).isEqualTo(originalUsername);
    }

    @Test
    void updateUser_withBlankEmail_shouldNotUpdateEmail() {
        String originalEmail = testUserEntity.getEmail();
        testUserCreationDto.setUsername(null);
        testUserCreationDto.setEmail("   ");
        testUserCreationDto.setPassword(null);

        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUserEntity));
        when(userRepository.save(testUserEntity)).thenReturn(testUserEntity);
        when(userMapper.toDto(testUserEntity)).thenReturn(testUserDto);

        UserDto result = userService.updateUser(testUserId, testUserCreationDto);

        assertThat(result).isNotNull();
        verify(userRepository).save(testUserEntity);
        assertThat(testUserEntity.getEmail()).isEqualTo(originalEmail);
    }

    @Test
    void updateUser_withBlankPassword_shouldNotUpdatePassword() {
        String originalPassword = testUserEntity.getPassword();
        testUserCreationDto.setUsername(null);
        testUserCreationDto.setEmail(null);
        testUserCreationDto.setPassword("   ");

        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUserEntity));
        when(userRepository.save(testUserEntity)).thenReturn(testUserEntity);
        when(userMapper.toDto(testUserEntity)).thenReturn(testUserDto);

        UserDto result = userService.updateUser(testUserId, testUserCreationDto);

        assertThat(result).isNotNull();
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository).save(testUserEntity);
        assertThat(testUserEntity.getPassword()).isEqualTo(originalPassword);
    }

    @Test
    void updateUser_withSameUsername_shouldAllowUpdate() {

        testUserCreationDto.setUsername("testuser"); // same as current username
        testUserCreationDto.setEmail(null);
        testUserCreationDto.setPassword(null);

        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUserEntity));
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUserEntity));
        when(userRepository.save(testUserEntity)).thenReturn(testUserEntity);
        when(userMapper.toDto(testUserEntity)).thenReturn(testUserDto);

        UserDto result = userService.updateUser(testUserId, testUserCreationDto);

        assertThat(result).isNotNull();
        verify(userRepository).save(testUserEntity);
        assertThat(testUserEntity.getUsername()).isEqualTo("testuser");
    }

    @Test
    void updateUser_withSameEmail_shouldAllowUpdate() {
        testUserCreationDto.setUsername(null);
        testUserCreationDto.setEmail("test@example.com"); // same as current email
        testUserCreationDto.setPassword(null);

        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUserEntity));
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUserEntity));
        when(userRepository.save(testUserEntity)).thenReturn(testUserEntity);
        when(userMapper.toDto(testUserEntity)).thenReturn(testUserDto);

        UserDto result = userService.updateUser(testUserId, testUserCreationDto);

        assertThat(result).isNotNull();
        verify(userRepository).save(testUserEntity);
        assertThat(testUserEntity.getEmail()).isEqualTo("test@example.com");
    }
}
