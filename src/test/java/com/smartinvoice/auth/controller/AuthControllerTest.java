package com.smartinvoice.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    private AuthenticationManager authenticationManager;
    private AuthController authController;

    @BeforeEach
    void setup() {
        authenticationManager = mock(AuthenticationManager.class);
        authController = new AuthController(authenticationManager, null);
    }

    @Test
    @DisplayName("Should return username for authenticated user")
    void me_authenticatedUser() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("john");

        ResponseEntity<?> response = authController.me(authentication);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(((Map<?, ?>) response.getBody()).get("username")).isEqualTo("john");
    }

    @Test
    @DisplayName("Should return 401 for unauthenticated user")
    void me_unauthenticatedUser() {
        ResponseEntity<?> response = authController.me(null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("Should login user and set session")
    void login_successful() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        Authentication auth = mock(Authentication.class);

        when(auth.getName()).thenReturn("john");
        when(request.getSession(true)).thenReturn(session);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);

        ResponseEntity<?> response = authController.login("john", "password", request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(((Map<?, ?>) response.getBody()).get("username")).isEqualTo("john");
        verify(session).setAttribute(eq(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY), any());
    }

    @Test
    @DisplayName("Should logout and invalidate session")
    void logout_successful() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getSession(false)).thenReturn(session);

        ResponseEntity<?> response = authController.logout(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(session).invalidate();
    }

    @Test
    @DisplayName("Should logout without session")
    void logout_noSession() {
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getSession(false)).thenReturn(null);

        ResponseEntity<?> response = authController.logout(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
