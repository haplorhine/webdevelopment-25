package at.technikum.springrestbackend.dto;

import at.technikum.springrestbackend.entity.Salutation;
import at.technikum.springrestbackend.entity.UserType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Setter

public class UserDto {

    @Enumerated(EnumType.STRING)
    private Salutation salutation;

    @Email
    private String email;

    @Size(min = 5)
    private String username;

    @Enumerated(EnumType.STRING)
    private UserType userType;

    @Enumerated(EnumType.STRING)
    private String country;


}
