package application.security.service;

import application.entity.user.UserEntity;
import application.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import static org.junit.jupiter.api.Assertions.*;


class CustomUserDetailsServiceTest {

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldLoadUserByUsername() {

        UserEntity user = UserEntity.builder()
                .username("James")
                .email("James@gmail.com")
                .password("james@#$%1234")
                .build();

        Mockito.when(userRepository.findByUsername("James"))
                .thenReturn(user);

        UserDetails result =
                customUserDetailsService.loadUserByUsername("James");

        assertNotNull(result);
        assertEquals("James", result.getUsername());

        Mockito.verify(userRepository, Mockito.times(1))
                .findByUsername("James");
    }

    @Test
    void shouldRaiseExceptionWhenUserIsNotFound() {

        UserEntity user = UserEntity.builder()
                .username("James")
                .email("James@gmail.com")
                .password("james@#$%1234")
                .build();

        String username = "adagedo";

        Mockito.when(userRepository.findByUsername(username))
                .thenReturn(null);

        UsernameNotFoundException exception =
                assertThrows(
                        UsernameNotFoundException.class,
                        () -> customUserDetailsService.loadUserByUsername(username)
                );

        assertEquals("user not found", exception.getMessage());

        Mockito.verify(userRepository)
                .findByUsername(username);
    }
}