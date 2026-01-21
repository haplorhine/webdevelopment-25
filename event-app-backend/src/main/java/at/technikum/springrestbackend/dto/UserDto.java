package at.technikum.springrestbackend.dto;

import at.technikum.springrestbackend.entity.Country;
import at.technikum.springrestbackend.entity.Salutation;
import at.technikum.springrestbackend.entity.UserType;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter

public class UserDto {

    private UUID id;

    private Salutation salutation;

    private String email;

    private String username;

    private UserType userType;

    private Country country;

    private UUID imageId;


}
