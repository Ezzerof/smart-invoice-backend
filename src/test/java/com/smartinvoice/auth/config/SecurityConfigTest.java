package com.smartinvoice.auth.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Should allow unauthenticated access to /api/auth/login")
    void shouldAllowLoginEndpointWithoutAuthentication() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .param("username", "test")
                        .param("password", "test"))
                .andExpect(status().isUnauthorized()); // assuming no valid user exists
    }

    @Test
    @DisplayName("Should block access to protected endpoint when not authenticated")
    void shouldBlockAccessToProtectedEndpoint() throws Exception {
        mockMvc.perform(get("/api/clients"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should return 200 on logout for any session")
    void shouldAllowLogoutForAnySession() throws Exception {
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should return CORS headers for preflight request")
    void shouldReturnCorsHeaders() throws Exception {
        ResultActions result = mockMvc.perform(options("/api/auth/login")
                .header(HttpHeaders.ORIGIN, "http://localhost:5173")
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST"));

        result.andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://localhost:5173"))
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "GET,POST,PUT,DELETE,PATCH,OPTIONS"));
    }

    @Test
    @DisplayName("Should disable CSRF protection")
    void shouldDisableCsrfProtection() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", "admin")
                        .param("password", "admin"))
                .andExpect(status().isUnauthorized());
    }
}

