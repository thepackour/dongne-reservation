package com.dongne.reservation.service;

import com.dongne.reservation.domain.User;
import com.dongne.reservation.enums.UserStatus;
import com.dongne.reservation.exception.DuplicateEmailException;
import com.dongne.reservation.repository.UserRepository;
import com.dongne.reservation.web.dto.SignupRequest;
import com.dongne.reservation.web.dto.SignupResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder pwEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder pwEncoder) {
        this.userRepository = userRepository;
        this.pwEncoder = pwEncoder;
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
                .reservationList(null).build();
        User res = userRepository.save(user);

        // 반환 DTO 생성
        SignupResponse signupResponse = new SignupResponse();
        signupResponse.setId(res.getId());
        signupResponse.setEmail(res.getEmail());
        return signupResponse;
    }

}
