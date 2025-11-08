package at.technikum.springrestbackend.dto;

import at.technikum.springrestbackend.entity.Country;
import at.technikum.springrestbackend.entity.Salutation;
import at.technikum.springrestbackend.entity.UserType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Setter

public class UserDto {

    private Salutation salutation;

    @Email
    private String email;

    @Size(min = 5)
    private String username;

    private UserType userType;

    private Country country;


}
