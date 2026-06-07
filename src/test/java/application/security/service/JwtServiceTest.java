package application.security.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    @Mock
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(
                jwtService,
                "ACCESS_TOKEN_EXPIRE_MINUTES",
                "15"
        );
        ReflectionTestUtils.setField(
                jwtService,
                "SECRETE_KEY",
                "ZmYxMjM0NTY3ODkwYWJjZGVmZmYxMjM0NTY3ODkwYWJjZGVm"
        );
        ReflectionTestUtils.setField(
                jwtService,
                "TOKEN_NAME",
                "jwtToken"
        );
    }
    @Test
    void ShouldGenerateToken() {
        String username = "James";
        String user_id = "19dd2a43-f882-497c-9ba2-dbe00da93863";
        String token = jwtService.generateToken(username, user_id);
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertFalse(token.isBlank());
    }


    @Test
    void ShouldNotValidateExpiredToken() {
        String username = "James";
        String user_id = "19dd2a43-f882-497c-9ba2-dbe00da93863";
        String token = jwtService.generateToken(username, user_id);
        assertNotNull(token);
        assertFalse(token.isBlank());
        boolean is_token = jwtService.validateToken(token, userDetails);
        assertFalse(is_token);
    }

    @Test
    void ShouldExtractUsername() {
        String username = "James";
        String user_id = "19dd2a43-f882-497c-9ba2-dbe00da93863";
        String token = jwtService.generateToken(username, user_id);
        assertNotNull(token);
        assertFalse(token.isBlank());
        String result_username = jwtService.extractUsername(token);
        assertEquals(username, result_username);
    }
}