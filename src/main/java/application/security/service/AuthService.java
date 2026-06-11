package application.security.service;

import application.dto.requestDto.Code;
import application.dto.requestDto.SignInRequest;
import application.dto.requestDto.SignUpRequest;
import org.springframework.http.ResponseEntity;

public interface AuthService {

    public ResponseEntity<?> signIn(SignInRequest signInRequest);

    public ResponseEntity<?> signUp(SignUpRequest signUpRequest);

    public ResponseEntity<?> verifyCode(Code codeDto);

    public ResponseEntity<?> logOut();

}
