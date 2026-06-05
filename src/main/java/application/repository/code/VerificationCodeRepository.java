package application.repository.code;

import application.entity.code.VerificationCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VerificationCodeRepository extends JpaRepository<VerificationCodeEntity, UUID> {
    VerificationCodeEntity findByUserEmail(String email);
    VerificationCodeEntity findByUserId(UUID user_id);
}
