package com.angebot.backend.auth;


import com.angebot.backend.auth.jwt.JwtService;
import com.angebot.backend.entity.User;
import com.angebot.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository repo;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public void register(RegisterRequest req) {
        String email = req.email().toLowerCase().trim();
        if (repo.existsByEmail(email)) {
            throw new IllegalArgumentException("EMAIL_ALREADY_EXISTS");
        }
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setPasswordHash(encoder.encode(req.password()));
        repo.save(newUser);
    }

    public AuthResponse login(LoginRequest req, long expSeconds) {
        String email = req.email().toLowerCase().trim();
        User user = repo.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("INVALID_CREDENTIALS"));
        if (!encoder.matches(req.password(), user.getPasswordHash())) {
            throw new IllegalArgumentException("INVALID_CREDENTIALS");
        }
        String token = jwt.generateToken(user.getEmail());
        return new AuthResponse(token, expSeconds);
    }
}