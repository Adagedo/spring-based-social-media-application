package application.controllers.auth;

import application.dto.requestDto.Code;
import application.dto.requestDto.SignInRequest;
import application.dto.requestDto.SignUpRequest;
import application.security.service.AuthServiceImplementation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth/")
public class AuthController {

    private final AuthServiceImplementation authServiceImplementation;

    public AuthController(AuthServiceImplementation authServiceImplementation) {
        this.authServiceImplementation = authServiceImplementation;
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@RequestBody SignUpRequest signUpRequest){
        return  this.authServiceImplementation.signUp(signUpRequest);
    }

    @PostMapping("/sign-in")
    public ResponseEntity<?> singIn(@RequestBody SignInRequest signInRequest){
        return this.authServiceImplementation.signIn(signInRequest);
    }

    @PostMapping("/verify-code")
    public ResponseEntity<?> verifyCode(@RequestBody  Code codeDto){
        return this.authServiceImplementation.verifyCode(codeDto);
    }

    @GetMapping("/sign-out")
    public ResponseEntity<?> logOut(){
        return this.authServiceImplementation.logOut();
    }
}
