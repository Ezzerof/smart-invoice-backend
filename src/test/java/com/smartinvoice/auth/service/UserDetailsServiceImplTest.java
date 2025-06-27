package com.smartinvoice.auth.service;

import com.smartinvoice.auth.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserDetailsServiceImplTest {

    private UserService userService;
    private UserDetailsServiceImpl userDetailsService;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        userDetailsService = new UserDetailsServiceImpl(userService);
    }

    @Test
    @DisplayName("Should load user by username - happy path")
    void loadUserByUsername_shouldReturnUserDetails() {
        User user = User.builder()
                .username("john_doe")
                .password("encodedPassword")
                .build();

        when(userService.findByUsername("john_doe")).thenReturn(user);

        UserDetails result = userDetailsService.loadUserByUsername("john_doe");

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("john_doe");
        assertThat(result.getPassword()).isEqualTo("encodedPassword");
        assertThat(result.getAuthorities()).anyMatch(auth -> auth.getAuthority().equals("ROLE_USER"));
    }

    @Test
    @DisplayName("Should throw UsernameNotFoundException when user not found - unhappy path")
    void loadUserByUsername_userNotFound_shouldThrow() {
        when(userService.findByUsername("not_found")).thenThrow(new UsernameNotFoundException("User not found"));

        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("not_found"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    @DisplayName("Should throw when empty username is given - edge case")
    void loadUserByUsername_emptyUsername_shouldThrow() {
        when(userService.findByUsername("")).thenThrow(new UsernameNotFoundException("User not found"));

        assertThatThrownBy(() -> userDetailsService.loadUserByUsername(""))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found");
    }
}
