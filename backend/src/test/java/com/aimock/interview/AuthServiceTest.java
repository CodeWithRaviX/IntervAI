package com.aimock.interview;

import com.aimock.interview.dto.auth.LoginRequest;
import com.aimock.interview.dto.auth.LoginResponse;
import com.aimock.interview.dto.auth.RegisterRequest;
import com.aimock.interview.dto.auth.UserResponse;
import com.aimock.interview.entity.Role;
import com.aimock.interview.entity.RoleName;
import com.aimock.interview.entity.User;
import com.aimock.interview.exception.DuplicateResourceException;
import com.aimock.interview.repository.RoleRepository;
import com.aimock.interview.repository.UserRepository;
import com.aimock.interview.security.JwtTokenProvider;
import com.aimock.interview.security.UserPrincipal;
import com.aimock.interview.service.AuditLogService;
import com.aimock.interview.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private AuthServiceImpl authService;

    private User sampleUser;
    private Role sampleRole;

    @BeforeEach
    void setUp() {
        sampleRole = new Role(1L, RoleName.ROLE_USER);
        sampleUser = User.builder()
                .id(UUID.randomUUID())
                .email("alex@example.com")
                .password("encoded_pass")
                .fullName("Alex Developer")
                .enabled(true)
                .accountNonLocked(true)
                .roles(Set.of(sampleRole))
                .build();
    }

    @Test
    @DisplayName("Should successfully register a new user")
    void testRegisterSuccess() {
        RegisterRequest request = RegisterRequest.builder()
                .email("alex@example.com")
                .password("password123")
                .fullName("Alex Developer")
                .build();

        when(userRepository.existsByEmail("alex@example.com")).thenReturn(false);
        when(roleRepository.findByName(RoleName.ROLE_USER)).thenReturn(Optional.of(sampleRole));
        when(passwordEncoder.encode("password123")).thenReturn("encoded_pass");
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);

        UserResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("alex@example.com", response.getEmail());
        assertEquals("Alex Developer", response.getFullName());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception on duplicate email registration")
    void testRegisterDuplicateEmail() {
        RegisterRequest request = RegisterRequest.builder()
                .email("alex@example.com")
                .password("password123")
                .fullName("Alex Developer")
                .build();

        when(userRepository.existsByEmail("alex@example.com")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should successfully login and return JWT token")
    void testLoginSuccess() {
        LoginRequest request = LoginRequest.builder()
                .email("alex@example.com")
                .password("password123")
                .build();

        Authentication auth = mock(Authentication.class);
        UserPrincipal principal = UserPrincipal.create(sampleUser);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
        when(tokenProvider.generateToken(auth)).thenReturn("mocked.jwt.token");
        when(userRepository.findByEmail("alex@example.com")).thenReturn(Optional.of(sampleUser));

        LoginResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("mocked.jwt.token", response.getAccessToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals("alex@example.com", response.getEmail());
    }
}
