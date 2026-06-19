package application.controllers.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import application.dto.requestDto.Code;
import application.dto.requestDto.SignInRequest;
import application.dto.requestDto.SignUpRequest;
import application.security.service.AuthServiceImplementation;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth/")
public class AuthController {

    private final AuthServiceImplementation authServiceImplementation;

    public AuthController(AuthServiceImplementation authServiceImplementation) {
        this.authServiceImplementation = authServiceImplementation;
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@Valid @RequestBody SignUpRequest signUpRequest){
        return  this.authServiceImplementation.signUp(signUpRequest);
    }

    @PostMapping("/sign-in")
    public ResponseEntity<?> singIn(@Valid @RequestBody SignInRequest signInRequest){
        return this.authServiceImplementation.signIn(signInRequest);
    }

    @PostMapping("/verify-code")
    public ResponseEntity<?> verifyCode(@Valid @RequestBody  Code codeDto){
        return this.authServiceImplementation.verifyCode(codeDto);
    }

    @GetMapping("/sign-out")
    public ResponseEntity<?> logOut(){
        return this.authServiceImplementation.logOut();
    }
}
