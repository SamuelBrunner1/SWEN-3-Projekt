package at.technikum.swen_brunner_wydra.controller;

import at.technikum.swen_brunner_wydra.entity.User;
import at.technikum.swen_brunner_wydra.service.AuthService;
import at.technikum.swen_brunner_wydra.service.JwtService;
import at.technikum.swen_brunner_wydra.service.dto.LoginRequest;
import at.technikum.swen_brunner_wydra.service.dto.RegisterRequest;
import at.technikum.swen_brunner_wydra.service.dto.TokenResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    public AuthController(AuthService authService, JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody RegisterRequest request) {
        authService.register(request.getEmail(), request.getPassword());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest request) {
        User user = authService.authenticate(request.getEmail(), request.getPassword());
        String token = jwtService.generateToken(user.getId());
        return ResponseEntity.ok(new TokenResponse(token));
    }
}
