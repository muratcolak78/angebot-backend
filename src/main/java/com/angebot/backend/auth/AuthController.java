package com.angebot.backend.auth;


import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
//@CrossOrigin("*")
public class AuthController {

    private final AuthService authService;
    private final long expMinutes;

    public AuthController(AuthService authService,
                          @Value("${app.jwt.exp-minutes}") long expMinutes) {
        this.authService = authService;
        this.expMinutes = expMinutes;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        authService.register(req);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest req) {
        long expSeconds = expMinutes * 60;
        return authService.login(req, expSeconds);
    }
}