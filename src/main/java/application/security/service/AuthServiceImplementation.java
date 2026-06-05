package application.security.service;


import application.dto.requestDto.Code;
import application.dto.requestDto.SignInRequest;
import application.dto.requestDto.SignUpRequest;
import application.dto.responseDto.SignInResponse;
import application.entity.code.VerificationCodeEntity;
import application.entity.user.UserEntity;
import application.transactions.auth.CreateUserTransaction;
import application.transactions.auth.VerifyCodeTransaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.webmvc.autoconfigure.WebMvcProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class AuthServiceImplementation implements AuthService{

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    private final VerifyCodeTransaction verifyCodeTransaction;

    private final CreateUserTransaction createUserTransaction;

    private final PasswordEncoder passwordEncoder;

    private final  String TOKEN_NAME;


    public AuthServiceImplementation(JwtService jwtService, AuthenticationManager authenticationManager, VerifyCodeTransaction verifyCodeTransaction,
                                     CreateUserTransaction createUserTransaction,
                                     PasswordEncoder passwordEncoder,
                                     @Value("${jwt.token_name}") String tokenName) {
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.verifyCodeTransaction = verifyCodeTransaction;
        this.createUserTransaction = createUserTransaction;
        this.passwordEncoder = passwordEncoder;
        TOKEN_NAME = tokenName;
    }

    @Override
    public ResponseEntity<?> signUp(SignUpRequest signUpRequest) {

        int verification_code = (int) ((Math.random() * 900000) + 100000);
        Optional<UserEntity> optionalUser = this.createUserTransaction.findUserByEmail(signUpRequest.email());
        UserEntity user = null;

        if (optionalUser.isPresent()){
            user = optionalUser.get();
            if (!user.is_verified()) {
                this.verifyCodeTransaction.SaveVerifiedUserCode(signUpRequest, user, String.valueOf(verification_code));
                return ResponseEntity.status(HttpStatus.OK)
                        .body("User exists but is unverified. A new verification email has been sent.");
            }
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Error: This email is already registered and verified.");
        }

        try {
            UserEntity saved_user = this.createUserTransaction.saveUserAfterSingUp(signUpRequest);
            this.verifyCodeTransaction.SaveVerifiedUserCode(signUpRequest, saved_user, String.valueOf(verification_code));

            System.out.println("Verification Code: " + verification_code);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("User registered successfully. Please verify your email.");

        } catch (Exception e) {
            log.info("database error {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Server error occurred during signup.");
        }
    }

    @Override
    public ResponseEntity<?> signIn(SignInRequest signInRequest) {

        Optional<UserEntity>  optionalUser = this.createUserTransaction.getOptionalUser(signInRequest.email());
        if(optionalUser.isEmpty()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("invalid credentials");
        }
        UserEntity user = optionalUser.get();
        if(!this.passwordEncoder.matches(signInRequest.password(), user.getPassword())){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("invalid email or password");
        }
        Authentication authentication = this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(), signInRequest.password()
                )
        );
        if(user.is_verified() && authentication.isAuthenticated()){

            String token = this.jwtService.generateToken(user.getUsername(), String.valueOf(user.getId()));
            ResponseCookie cookie = ResponseCookie.from(TOKEN_NAME, token)
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .maxAge(3600)
                    .sameSite("Lax")
                    .build();

            this.createUserTransaction.UpdateUserStatus(user);
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(new SignInResponse(
                            user.getId(),
                            user.getUsername(),
                            user.getEmail(),
                            user.getStatus()
                    ));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("invalid credentials");

    }

    @Override
    public ResponseEntity<?> verifyCode(Code codeDto) {
        Optional<UserEntity> optionalUser = this.createUserTransaction.getOptionalUser(codeDto.email());
        if(optionalUser.isEmpty()){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("user with email " + codeDto.email() + " is not registered");
        }
        UserEntity user = optionalUser.get();
        VerificationCodeEntity code = this.verifyCodeTransaction.findCodeByUserId(user.getId());
        if(codeDto.code().isEmpty()){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("verification code used");
        }
        if(!user.getId().equals(code.getUser().getId())){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("invalid code");
        }
        if (code.getType().equals("email_verification")){
            if(passwordEncoder.matches(codeDto.code(), code.getCode())){
                code.setCode(null);
                this.verifyCodeTransaction.updateCode(code);
                this.createUserTransaction.UpdateUserStatus(user);
                return ResponseEntity.status(HttpStatus.ACCEPTED).body("email verified successfully");
            }
            ResponseEntity.status(HttpStatus.FORBIDDEN).body("invalid code");
        }
        return  ResponseEntity.status(HttpStatus.FORBIDDEN).body("invalid verification code");
    }

    @Override
    public ResponseEntity<?> logOut() {
        ResponseCookie cookie = ResponseCookie.from(TOKEN_NAME, "")
                .httpOnly(true)
                .secure(false)
                .maxAge(0)
                .path("/")
                .sameSite("lax")
                .build();

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body("user logged out successfully");
    }

//    @Override
//    public ResponseEntity<?> resetPassword(String resetCode) {
//        return null;
//    }



}
