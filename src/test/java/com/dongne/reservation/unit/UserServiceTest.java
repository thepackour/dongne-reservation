package com.dongne.reservation.unit;

import com.dongne.reservation.domain.User;
import com.dongne.reservation.enums.UserRole;
import com.dongne.reservation.enums.UserStatus;
import com.dongne.reservation.exception.DuplicateEmailException;
import com.dongne.reservation.exception.LoginFailedException;
import com.dongne.reservation.exception.NotFoundException;
import com.dongne.reservation.jwt.JwtTokenProvider;
import com.dongne.reservation.repository.UserRepository;
import com.dongne.reservation.service.UserService;
import com.dongne.reservation.web.dto.LoginRequest;
import com.dongne.reservation.web.dto.SignupRequest;
import com.dongne.reservation.web.dto.SignupResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

// mockito로 mock 객체 만들어서 unit test
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder pwEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private UserService userService;

    private final User user = User.builder()
            .id(1L)
            .email("asdf@gmail.com")
            .password("encrypted")
            .name("testuser")
            .status(UserStatus.ACTIVE)
            .role(UserRole.USER)
            .reservationList(null).build();

    @Test
    void registerSuccess() {
        SignupRequest request = new SignupRequest("asdf@gmail.com", "not_encrypted", "testuser");
        SignupResponse answer = SignupResponse.builder()
                .id(1L)
                .email(request.getEmail())
                .build();


        when(userRepository.save(any(User.class)))
                .thenReturn(user);
        when(pwEncoder.encode(any(String.class)))
                .thenReturn("encrypted");

        SignupResponse res = userService.register(request);
        assertThat(res.getEmail()).isEqualTo(request.getEmail());
    }

    @Test
    void registerFailureByDuplicateEmail() {
        SignupRequest request = new SignupRequest("asdf@gmail.com", "not_encrypted", "testuser");

        User new_user = User.builder()
                .id(2L)
                .email("asdf@gmail.com")
                .password("encrypted")
                .name("testuser2")
                .status(UserStatus.ACTIVE)
                .reservationList(null).build();

        when(userRepository.findByEmail("asdf@gmail.com"))
                .thenReturn(Optional.of(new_user));
        lenient().when(userRepository.save(any(User.class)))
                .thenReturn(user);

        DuplicateEmailException ex = Assertions.assertThrows(DuplicateEmailException.class, () -> {
            SignupResponse res = userService.register(request);
            System.out.println(res);
        });
        assertThat(ex.getMessage()).contains("이미 사용 중인 이메일입니다.");
    }

    @Test
    void loginSuccess() {
        LoginRequest request = new LoginRequest("asdf@gmail.com", "encrypted");

        when(userRepository.findByEmail("asdf@gmail.com"))
                .thenReturn(Optional.ofNullable(user));
        when(pwEncoder.matches(any(String.class), any(String.class)))
                .thenAnswer(invocation -> {
                    String arg1 = invocation.getArgument(0);
                    String arg2 = invocation.getArgument(1);
                    return arg1.equals(arg2);
                });
        when(jwtTokenProvider.createToken(any(String.class), any()))
                .thenReturn("token");

        String res = userService.login(request);
        assertThat(res.isBlank()).isFalse();
    }

    @Test
    void loginPwFailure() {
        LoginRequest request = new LoginRequest("asdf@gmail.com", "wrongpw");

        when(userRepository.findByEmail("asdf@gmail.com"))
                .thenReturn(Optional.ofNullable(user));
        lenient().when(pwEncoder.encode(any(String.class)))
                .thenReturn("encrypted");

        Assertions.assertThrows(LoginFailedException.class, () -> {
            String res = userService.login(request);
            System.out.println(res);
        });
    }

    @Test
    void loginNonExistentEmail() {
        LoginRequest request = new LoginRequest("whoami@gmail.com", "encrypted");

        when(userRepository.findByEmail(any()))
                .thenReturn(Optional.empty());
        lenient().when(pwEncoder.encode(any(String.class)))
                .thenReturn("encrypted");

        Assertions.assertThrows(LoginFailedException.class, () -> {
            String res = userService.login(request);
            System.out.println(res);
        });
    }
}
