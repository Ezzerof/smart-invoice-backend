package com.smartinvoice.auth.service;

import com.smartinvoice.auth.dto.UserRequestDto;
import com.smartinvoice.auth.entity.User;
import com.smartinvoice.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        userService = new UserService(userRepository, passwordEncoder);
    }

    @Test
    @DisplayName("Should register user with encoded password")
    void registerUser_shouldSucceed() {
        UserRequestDto dto = new UserRequestDto("admin", "secret");
        when(passwordEncoder.encode("secret")).thenReturn("encodedSecret");
        User savedUser = User.builder().id(1L).username("admin").password("encodedSecret").build();
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = userService.registerUser(dto);

        assertThat(result.getUsername()).isEqualTo("admin");
        assertThat(result.getPassword()).isEqualTo("encodedSecret");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should return user by username")
    void findByUsername_shouldReturnUser() {
        User user = User.builder().id(1L).username("admin").password("pass").build();
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));

        User result = userService.findByUsername("admin");

        assertThat(result.getUsername()).isEqualTo("admin");
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void findByUsername_shouldThrowWhenNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findByUsername("unknown"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found");
    }
}
