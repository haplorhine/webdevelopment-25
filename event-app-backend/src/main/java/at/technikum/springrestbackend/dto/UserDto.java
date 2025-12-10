package at.technikum.springrestbackend.dto;

import at.technikum.springrestbackend.entity.Country;
import at.technikum.springrestbackend.entity.Salutation;
import at.technikum.springrestbackend.entity.UserType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter

public class UserDto {

    private UUID id;

    @NotNull
    private Salutation salutation;

    @Email
    @NotBlank
    private String email;

    @Size(min = 4)
    private String username;

    @NotNull
    private UserType userType;

    @Size(min = 5)
    private String password;

    @NotNull
    private Country country;


}
