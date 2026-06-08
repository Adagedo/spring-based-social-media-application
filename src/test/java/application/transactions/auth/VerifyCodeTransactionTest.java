package application.transactions.auth;

import application.dto.requestDto.SignUpRequest;
import application.entity.code.VerificationCodeEntity;
import application.entity.user.UserEntity;
import application.repository.code.VerificationCodeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class VerifyCodeTransactionTest {

    @InjectMocks
    private VerifyCodeTransaction verifyCodeTransaction;

    @Mock
    private VerificationCodeRepository verificationCodeRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveVerifiedUserCode() {

        SignUpRequest request = new SignUpRequest(
                "John",
                "John@Gmail.com",
                "john1234!@$@"
        );
        UserEntity user = UserEntity.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .build();

        int verification_code = (int) ((Math.random() * 900000) + 100000);
        String code_toString = String.valueOf(verification_code);

        VerificationCodeEntity code = VerificationCodeEntity.builder()
                .user(user)
                .user_email(user.getEmail())
                .code(code_toString)
                .type("email_verification")
                .build();

        when(verificationCodeRepository.save(any(VerificationCodeEntity.class))).thenReturn(code);
        assertEquals(code.getUser_email(), user.getEmail());
        verifyCodeTransaction.SaveVerifiedUserCode(request, user, code_toString);
        verify(verificationCodeRepository, times(1)).save(any(VerificationCodeEntity.class));
    }

    @Test
    void findCodeByUserId() {

        SignUpRequest request = new SignUpRequest(
                "John",
                "John@Gmail.com",
                "john1234!@$@"
        );
        UserEntity user = UserEntity.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .build();

        int verification_code = (int) ((Math.random() * 900000) + 100000);
        String code_toString = String.valueOf(verification_code);

        UUID user_id = user.getId();

        VerificationCodeEntity code = VerificationCodeEntity.builder()
                .user(user)
                .user_email(user.getEmail())
                .code(code_toString)
                .type("email_verification")
                .build();

        when(verificationCodeRepository.findByUserId(user_id)).thenReturn(code);
        VerificationCodeEntity codeEntity = verifyCodeTransaction.findCodeByUserId(user_id);
        assertNotNull(codeEntity);
        assertAll(
                "code_user",
                () ->  assertEquals(code.getUser_email(), codeEntity.getUser_email()),
                () -> assertEquals(code.getUser(), codeEntity.getUser()),
                () -> assertEquals(code.getType(), codeEntity.getType())
        );

        verify(verificationCodeRepository, times(1)).findByUserId(user_id);



    }

    @Test
    void updateCode() {
    }
}