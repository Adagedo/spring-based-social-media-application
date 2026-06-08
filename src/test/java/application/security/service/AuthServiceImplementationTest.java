package application.security.service;

import application.dto.requestDto.SignInRequest;
import application.dto.requestDto.SignUpRequest;
import application.dto.responseDto.SignInResponse;
import application.entity.user.UserEntity;
import application.transactions.auth.CreateUserTransaction;
import application.transactions.auth.VerifyCodeTransaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceImplementationTest {

    @InjectMocks
    private AuthServiceImplementation authServiceImplementation;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private VerifyCodeTransaction verifyCodeTransaction;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private CreateUserTransaction createUserTransaction;

    @Mock
    private JwtService jwtService;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(
                authServiceImplementation,
                "TOKEN_NAME",
                "session"
        );
    }

    @Test
    void ShouldSignUpNewUser() {

        int verification_code = (int) ((Math.random() * 900000) + 100000);
        SignUpRequest request = new SignUpRequest(
                "Donald Trump",
                "traump@gmail.com",
                "Donald@34413!##121"
        );

        UserEntity user = UserEntity.builder()
                .username("Trump")
                .email("Trump@gmail.com")
                .password("Trump1234!@#$")
                .build();

        UserEntity user_ = null;
        when(createUserTransaction.findUserByEmail(user.getEmail())).thenReturn(Optional.of(user));
        Optional<UserEntity> optionalUser = this.createUserTransaction.findUserByEmail(request.email());
        if (optionalUser.isPresent()){
            user_ = optionalUser.get();
        }
        assertNull(user_);
        ResponseEntity<?> response = authServiceImplementation.signUp(request);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(
                "User registered successfully. Please verify your email.",
                response.getBody()
        );
        verify(createUserTransaction, times(1)).saveUserAfterSingUp(request);

    }

    @Test
    void ShouldSendVerificationCodeToExistingUsers() {
        int verification_code = (int) ((Math.random() * 900000) + 100000);
        SignUpRequest request = new SignUpRequest(
                "Donald Trump",
                "traump@gmail.com",
                "Donald@34413!##121"
        );

        UserEntity user = UserEntity.builder()
                .username("Donald Trump")
                .email("traump@gmail.com")
                .password("Donald@34413!##121")
                .build();

        UserEntity user_ = null;
        when(createUserTransaction.findUserByEmail(user.getEmail())).thenReturn(Optional.of(user));
        Optional<UserEntity> optionalUser = this.createUserTransaction.findUserByEmail(request.email());
        if (optionalUser.isPresent()){
            user_ = optionalUser.get();
        }
        assertNotNull(user_);
        assertFalse(user_.is_verified());
        ResponseEntity<?> response = authServiceImplementation.signUp(request);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(
                "User exists but is unverified. A new verification email has been sent.",
                response.getBody()
        );


    }

    @Test
    void ShouldNotSignUpAlreadyRegisteredAndVerifiedUsers() {
        SignUpRequest request = new SignUpRequest(
                "Donald Trump",
                "traump@gmail.com",
                "Donald@34413!##121"
        );

        UserEntity user = UserEntity.builder()
                .username("Donald Trump")
                .email("traump@gmail.com")
                .password("Donald@34413!##121")
                .is_verified(true)
                .build();

        UserEntity user_ = null;
        when(createUserTransaction.findUserByEmail(user.getEmail())).thenReturn(Optional.of(user));
        Optional<UserEntity> optionalUser = this.createUserTransaction.findUserByEmail(request.email());
        if (optionalUser.isPresent()){
            user_ = optionalUser.get();
        }
        assertNotNull(user_);
        assertTrue(user_.is_verified());
        ResponseEntity<?> response = authServiceImplementation.signUp(request);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(
                "Error: This email is already registered and verified.",
                response.getBody()
        );
    }

    @Test
    void shouldLoginSuccessfully() {

        SignInRequest request =
                new SignInRequest("Joe@gmail.com", "password");

        UserEntity user = UserEntity.builder()
                .username("Joe")
                .email("Joe@gmail.com")
                .password(passwordEncoder.encode(request.password()))
                .status("active")
                .is_verified(true)
                .build();

        Authentication authentication =
                mock(Authentication.class);

        when(createUserTransaction.findUserByEmail("Joe@gmail.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "password",
                user.getPassword()
        )).thenReturn(true);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);

        when(authentication.isAuthenticated()).thenReturn(true);

        when(jwtService.generateToken(user.getUsername(), String.valueOf(user.getId()))).thenReturn(any(String.class));

        ResponseEntity<?> response = authServiceImplementation.signIn(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertEquals(new SignInResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getStatus()
        ), response.getBody());
        var body =  response.getBody();

        assertNotNull(body);
//        assertEquals(user.getUsername(), body();

        verify(jwtService, times(1)).generateToken(user.getUsername(), String.valueOf(user.getId()));

        verify(createUserTransaction).UpdateUserStatus(user);
    }
    @Test
    void shouldReturnUnauthorizedWhenPasswordIsInvalid() {

        SignInRequest request =
                new SignInRequest("Joe@gmail.com", "wrong-password");

        UserEntity user = UserEntity.builder()
                .email("Joe@gmail.com")
                .password("encoded-password")
                .build();

        when(createUserTransaction.findUserByEmail("Joe@gmail.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "wrong-password",
                "encoded-password"
        )).thenReturn(false);

        ResponseEntity<?> response = authServiceImplementation.signIn(request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("invalid credentials", response.getBody());

        verify(authenticationManager, never())
                .authenticate(any());
    }

    @Test
    void verifyCode() {

    }

    
    @Test
    void logOut() {
    }
}