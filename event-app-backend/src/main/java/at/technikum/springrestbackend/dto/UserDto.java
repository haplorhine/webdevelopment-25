package at.technikum.springrestbackend.dto;


import at.technikum.springrestbackend.entity.UserType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class UserDto {

    private Long userId;
    private String salutation;
    private String email;
    private String username;
    private UserType userType;
    private String country;


}
