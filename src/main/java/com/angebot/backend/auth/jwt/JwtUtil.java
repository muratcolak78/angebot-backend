package com.angebot.backend.auth.jwt;

import com.angebot.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtUtil {
    private final UserRepository userRepo;
    public UUID extractUserId(String subject) {  // JWT sub → email → userId
        // Token decode logic veya UserRepo.findByEmail(subject).getId()
        return userRepo.findByEmail(subject).orElseThrow().getId();
    }
    public UUID getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();  // JWT subject
        return userRepo.findByEmail(email).orElseThrow().getId();
    }

}