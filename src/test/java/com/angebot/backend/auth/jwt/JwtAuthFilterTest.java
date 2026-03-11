package com.angebot.backend.auth.jwt;

import com.angebot.backend.repository.UserRepository;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthFilter jwtAuthFilter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldContinueFilterChainWhenAuthorizationHeaderIsNull() throws Exception {
        jwtAuthFilter.doFilter(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verifyNoInteractions(jwtService);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void shouldContinueFilterChainWhenAuthorizationHeaderDoesNotStartWithBearer() throws Exception {
        request.addHeader("Authorization", "Basic abc123");

        jwtAuthFilter.doFilter(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verifyNoInteractions(jwtService);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void shouldSetAuthenticationWhenTokenIsValidAndNoAuthenticationExists() throws Exception {
        String token = "valid-token";
        String email = "test@example.com";
        request.addHeader("Authorization", "Bearer " + token);

        when(jwtService.extractEmail(token)).thenReturn(email);

        jwtAuthFilter.doFilter(request, response, filterChain);

        verify(jwtService, times(1)).extractEmail(token);
        verify(filterChain, times(1)).doFilter(request, response);

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertInstanceOf(UsernamePasswordAuthenticationToken.class, authentication);
        assertEquals(email, authentication.getPrincipal());
        assertTrue(authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void shouldNotOverrideAuthenticationWhenAlreadyExists() throws Exception {
        String token = "valid-token";
        String email = "test@example.com";
        request.addHeader("Authorization", "Bearer " + token);

        var existingAuth = new UsernamePasswordAuthenticationToken("already-authenticated-user", null);
        SecurityContextHolder.getContext().setAuthentication(existingAuth);

        when(jwtService.extractEmail(token)).thenReturn(email);

        jwtAuthFilter.doFilter(request, response, filterChain);

        verify(jwtService, times(1)).extractEmail(token);
        verify(filterChain, times(1)).doFilter(request, response);

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertSame(existingAuth, authentication);
        assertEquals("already-authenticated-user", authentication.getPrincipal());
    }

    @Test
    void shouldContinueFilterChainWhenExtractedEmailIsNull() throws Exception {
        String token = "valid-token";
        request.addHeader("Authorization", "Bearer " + token);

        when(jwtService.extractEmail(token)).thenReturn(null);

        jwtAuthFilter.doFilter(request, response, filterChain);

        verify(jwtService, times(1)).extractEmail(token);
        verify(filterChain, times(1)).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void shouldReturnUnauthorizedWhenJwtServiceThrowsException() throws Exception {
        String token = "invalid-token";
        request.addHeader("Authorization", "Bearer " + token);

        when(jwtService.extractEmail(token)).thenThrow(new RuntimeException("Token expired"));

        jwtAuthFilter.doFilter(request, response, filterChain);

        verify(jwtService, times(1)).extractEmail(token);
        verify(filterChain, never()).doFilter(request, response);

        assertEquals(401, response.getStatus());
        assertEquals("application/json", response.getContentType());
        assertEquals("{\"error\": \"Invalid or expired token!\"}", response.getContentAsString());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}