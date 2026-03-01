package com.angebot.backend.auth.jwt;

import com.angebot.backend.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserRepository userRepository;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String auth=request.getHeader("Authorization");

        if(auth==null || !auth.startsWith("Bearer")){
            filterChain.doFilter(request,response);
            return;
        }
        // if there is authorization in the Header and start with Bearer
        String token= auth.substring(7);
        log.info("token is -> {}",token);

        try{
            // we are extracting the email
            String email=jwtService.extractEmail(token);
            log.info(email);
            // is there user?
            if(email!=null && SecurityContextHolder.getContext().getAuthentication()==null){
              //  log.info("email i snot null->{}", email);
                // we are generating a new token
                var authToken=new UsernamePasswordAuthenticationToken(
                        email, null, List.of(new SimpleGrantedAuthority("ROLE_USER"))
                );
                //log.info("authToken is -->{}", authToken);
                // then saying to Security service that: he this guy can make what he wants
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }catch (Exception e){
            log.error("JWT error: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Invalid or expired token!\"}");
            return;
        }
        filterChain.doFilter(request,response);
    }
}
