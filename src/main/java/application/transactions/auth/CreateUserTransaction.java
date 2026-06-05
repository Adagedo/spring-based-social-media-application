package application.transactions.auth;

import application.dto.requestDto.SignInRequest;
import application.dto.requestDto.SignUpRequest;
import application.entity.code.VerificationCodeEntity;
import application.entity.user.UserEntity;
import application.repository.user.UserRepository;
import jakarta.transaction.Transactional;
import org.apache.catalina.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

@Service
public class CreateUserTransaction {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final VerifyCodeTransaction verifyCodeTransaction;

    public CreateUserTransaction(UserRepository userRepository, PasswordEncoder passwordEncoder, VerifyCodeTransaction verifyCodeTransaction) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.verifyCodeTransaction = verifyCodeTransaction;
    }

    @Transactional
    public UserEntity saveUserAfterSingUp(SignUpRequest signUpRequest){

        UserEntity new_user = UserEntity.builder()
                .username(signUpRequest.username())
                .email(signUpRequest.email())
                .password(passwordEncoder.encode(signUpRequest.password()))
                .lastLoginAt(Timestamp.from(Instant.now()))
                .is_verified(false)
                .build();

        return this.userRepository.save(new_user);
    }

    public  Optional<UserEntity> findUserByEmail(String email){
        return this.userRepository.findUserByEmail(email);
    }

    public Optional<UserEntity> getOptionalUser(String email){
        return this.userRepository.findUserByEmail(email);

    }

    @Transactional
    public void UpdateUserStatus(UserEntity user){
        user.setStatus("active");
        user.set_verified(true);
        this.userRepository.save(user);
    }
}
