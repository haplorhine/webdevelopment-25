package at.technikum.springrestbackend.services;

import at.technikum.springrestbackend.dto.TokenRequestDto;
import at.technikum.springrestbackend.dto.TokenResponseDto;
import at.technikum.springrestbackend.security.jwt.JwtIssuer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtIssuer jwtIssuer;

    public TokenResponseDto authenticate(TokenRequestDto authRequest){
        UUID uuid = UUID.randomUUID();
        var token = jwtIssuer.issue(uuid, authRequest.getEmail(), "USER");
        return TokenResponseDto.builder()
                .token(token)
                .build();
    }

}

