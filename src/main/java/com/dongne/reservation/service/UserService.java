package com.dongne.reservation.service;

import com.dongne.reservation.domain.User;
import com.dongne.reservation.enums.UserRole;
import com.dongne.reservation.enums.UserStatus;
import com.dongne.reservation.exception.DuplicateEmailException;
import com.dongne.reservation.exception.LoginFailedException;
import com.dongne.reservation.jwt.JwtTokenProvider;
import com.dongne.reservation.repository.UserRepository;
import com.dongne.reservation.web.dto.LoginRequest;
import com.dongne.reservation.web.dto.SignupRequest;
import com.dongne.reservation.web.dto.SignupResponse;
import jakarta.validation.Valid;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder pwEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public UserService(UserRepository userRepository,
                       BCryptPasswordEncoder pwEncoder,
                       JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.pwEncoder = pwEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public SignupResponse register(SignupRequest signupRequest) {
        // 이메일 중복 확인
        if (userRepository.findByEmail(signupRequest.getEmail()).isPresent()) {
            throw new DuplicateEmailException("이미 사용 중인 이메일입니다.");
        }

        // 유저 객체 생성 후 save() 호출
        User user = User.builder()
                .email(signupRequest.getEmail())
                .password(pwEncoder.encode(signupRequest.getPassword()))
                .name(signupRequest.getName())
                .status(UserStatus.ACTIVE)
                .reservationList(null)
                .role(UserRole.USER).build();
        User res = userRepository.save(user);

        // 반환 DTO 생성
        SignupResponse signupResponse = new SignupResponse();
        signupResponse.setId(res.getId());
        signupResponse.setEmail(res.getEmail());
        return signupResponse;
    }

    public String login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new LoginFailedException("등록되지 않은 이메일입니다."));
        if (!pwEncoder.matches(request.getPassword(), user.getPassword())) throw new LoginFailedException("비밀번호가 틀렸습니다.");

        return jwtTokenProvider.createToken(user.getEmail(), List.of(user.getRole().toString()));
    }
}
