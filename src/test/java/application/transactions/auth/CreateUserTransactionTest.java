package application.transactions.auth;

import application.dto.requestDto.SignUpRequest;
import application.entity.user.UserEntity;
import application.repository.user.UserRepository;
import com.nimbusds.jose.JWEHeader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class CreateUserTransactionTest {

    @InjectMocks
    private CreateUserTransaction createUserTransaction;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveUserAfterSingUp() {

        SignUpRequest request = new SignUpRequest(
                "Joe",
                "Joe@gmail.com",
                "Joe123@#$"
        );

        UserEntity savedUser = UserEntity.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .build();
        when(userRepository.save(any(UserEntity.class))).thenReturn(savedUser);

        UserEntity result = createUserTransaction.saveUserAfterSingUp(request);
        assertNotNull(result);
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    void findUserByEmail() {
        UserEntity user = UserEntity.builder()
                .username("Joe")
                .email("Joe@gmail.com")
                .password(passwordEncoder.encode("Jow1234!@#"))
                .build();
        String email = "Joe@gmail.com";
        when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(user));
        Optional<UserEntity> result = createUserTransaction.findUserByEmail(email);
        assertNotNull(result);
        UserEntity current_user;
        current_user = result.orElse(null);
        assertAll(
                "users",
                () -> {
                    assert current_user != null;
                    assertEquals(user.getEmail(), current_user.getEmail());
                    assertEquals(user.getUsername(), current_user.getUsername());
                }
        );
        verify(userRepository, Mockito.times(1)).findUserByEmail(email);
    }


    @Test
    void updateUserStatus() {

        UserEntity user = UserEntity.builder()
                .username("Joe")
                .email("Joe@gmail.com")
                .password(passwordEncoder.encode("Jow1234!@#"))
                .build();
        user.setStatus("active");
        user.set_verified(true);
        when(userRepository.save(any(UserEntity.class))).thenReturn(user);
        createUserTransaction.UpdateUserStatus(user);
        verify(userRepository, times(1)).save(user);

    }
}