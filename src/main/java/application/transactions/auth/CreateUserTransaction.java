package application.transactions.auth;

import application.dto.requestDto.SignUpRequest;
import application.entity.user.UserEntity;
import application.repository.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;

@Service
public class CreateUserTransaction {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public CreateUserTransaction(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserEntity saveUserAfterSingUp(SignUpRequest signUpRequest){
        UserEntity new_user = UserEntity.builder()
                .username(signUpRequest.username())
                .email(signUpRequest.email())
                .password(passwordEncoder.encode(signUpRequest.password()))
                .lastLoginAt(Timestamp.from(Instant.now()))
                .build();

        return this.userRepository.save(new_user);
    }
}
