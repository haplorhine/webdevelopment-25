package at.technikum.springrestbackend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

@Getter
@Setter

public class User {

    @UuidGenerator
    private Long userId;

    @NotEmpty @Size(min = 6, max = 30)
    private String username;

    @Email
    private String email;

    @Enumerated(EnumType.STRING)
    private UserType userType;



}
