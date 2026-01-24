package at.technikum.springrestbackend.services;

import at.technikum.springrestbackend.dto.TokenRequestDto;
import at.technikum.springrestbackend.dto.TokenResponseDto;
import at.technikum.springrestbackend.security.UserPrincipal;
import at.technikum.springrestbackend.security.jwt.TokenIssuer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private TokenIssuer tokenIssuer;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private TokenRequestDto requestDto;
    private UserPrincipal principal;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        requestDto = new TokenRequestDto();
        requestDto.setUsername("john");
        requestDto.setPassword("secret");
        principal = new UserPrincipal(UUID.randomUUID(), "john", "encoded", "ROLE_USER", true);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void authenticate_setsSecurityContextAndReturnsToken() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);
        when(tokenIssuer.issue(principal.getId(), principal.getUsername(), principal.getRole())).thenReturn("jwt-token");

        TokenResponseDto response = authService.authenticate(requestDto);

        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isEqualTo(authentication);
        verify(tokenIssuer).issue(principal.getId(), principal.getUsername(), principal.getRole());
    }

    @Test
    void authenticate_whenAuthenticationFails_propagatesException() {
        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenThrow(new BadCredentialsException("invalid"));

        assertThatThrownBy(() -> authService.authenticate(requestDto))
                .isInstanceOf(BadCredentialsException.class);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(tokenIssuer, never()).issue(any(), any(), any());
    }
}
