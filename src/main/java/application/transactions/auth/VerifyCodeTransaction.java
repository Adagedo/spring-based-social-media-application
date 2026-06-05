package application.transactions.auth;

import application.dto.requestDto.SignUpRequest;
import application.entity.code.VerificationCodeEntity;
import application.entity.user.UserEntity;
import application.repository.code.VerificationCodeRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class VerifyCodeTransaction {

    private final PasswordEncoder passwordEncoder;

    private final VerificationCodeRepository verificationCodeRepository;

    public VerifyCodeTransaction(PasswordEncoder passwordEncoder, VerificationCodeRepository verificationCodeRepository) {
        this.passwordEncoder = passwordEncoder;
        this.verificationCodeRepository = verificationCodeRepository;
    }

    @Transactional
    public void SaveVerifiedUserCode(SignUpRequest dto, UserEntity user, String code){

        VerificationCodeEntity code_data = VerificationCodeEntity.builder()
                .user(user)
                .user_email(dto.email())
                .code(this.passwordEncoder.encode(code))
                .type("email_verification")
                .build();

        this.verificationCodeRepository.save(code_data);
    }

    public VerificationCodeEntity findCodeByUserId(UUID user_id){
        return this.verificationCodeRepository.findByUserId(user_id);
    }

    @Transactional
    public void updateCode(VerificationCodeEntity verificationCodeEntity){
        this.verificationCodeRepository.save(verificationCodeEntity);
    }
}
