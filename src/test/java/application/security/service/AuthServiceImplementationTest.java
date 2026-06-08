package application.security.service;

import application.entity.user.UserEntity;
import application.transactions.auth.CreateUserTransaction;
import application.transactions.auth.VerifyCodeTransaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

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
    void signUp() {

        UserEntity user = UserEntity.builder()
                .username("Trump")
                .email("Trump@gmail.com")
                .password("Trump1234!@#$")
                .build();

        when(createUserTransaction.findUserByEmail(user.getEmail())).thenReturn(Optional.of(user));
        assertAll(
                "users",
                () -> {
                    assertNotNull(user);
                    String username = user.getUsername();
                    assertNotNull(username);
                    assertAll(
                            "username",
                            () -> assertEquals(username, user.getUsername())
                    );

                },
                () -> {
                    String email = user.getEmail();
                    assertAll(
                            "email",
                            () -> assertNotNull(email),
                            () -> assertEquals(email, user.getEmail())
                    );
                }
        );
    }

    @Test
    void signIn() {
    }

    @Test
    void verifyCode() {
    }

    @Test
    void logOut() {
    }
}