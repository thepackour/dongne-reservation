package com.dongne.reservation.service;

import com.dongne.reservation.domain.Reservation;
import com.dongne.reservation.domain.User;
import com.dongne.reservation.enums.ReservationStatus;
import com.dongne.reservation.enums.UserRole;
import com.dongne.reservation.enums.UserStatus;
import com.dongne.reservation.exception.DuplicateEmailException;
import com.dongne.reservation.exception.LoginFailedException;
import com.dongne.reservation.exception.NotFoundException;
import com.dongne.reservation.jwt.JwtTokenProvider;
import com.dongne.reservation.repository.SpringDataJpaReservationRepository;
import com.dongne.reservation.repository.SpringDataJpaUserRepository;
import com.dongne.reservation.repository.UserRepository;
import com.dongne.reservation.web.dto.LoginRequest;
import com.dongne.reservation.web.dto.ReservationResponse;
import com.dongne.reservation.web.dto.SignupRequest;
import com.dongne.reservation.web.dto.SignupResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder pwEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final SpringDataJpaUserRepository springDataJpaUserRepository;
    private final SpringDataJpaReservationRepository springDataJpaReservationRepository;

    public UserService(UserRepository userRepository,
                       BCryptPasswordEncoder pwEncoder,
                       JwtTokenProvider jwtTokenProvider, SpringDataJpaUserRepository springDataJpaUserRepository,
                       SpringDataJpaReservationRepository springDataJpaReservationRepository) {
        this.userRepository = userRepository;
        this.pwEncoder = pwEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.springDataJpaUserRepository = springDataJpaUserRepository;
        this.springDataJpaReservationRepository = springDataJpaReservationRepository;
    }

    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
    public String login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new LoginFailedException("등록되지 않은 이메일입니다."));
        if (!pwEncoder.matches(request.getPassword(), user.getPassword())) throw new LoginFailedException("비밀번호가 틀렸습니다.");

        return jwtTokenProvider.createToken(user.getEmail(), List.of(user.getRole().toString()));
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> getMyReservations(Pageable pageable, ReservationStatus status, String token) {
        User user = springDataJpaUserRepository.findByEmail(jwtTokenProvider.getUsername(token))
                .orElseThrow(() -> new NotFoundException("유저를 찾을 수 없습니다."));

        Page<Reservation> res;
        if (status == null) res = springDataJpaReservationRepository.findByUser(user, pageable);
        else res = springDataJpaReservationRepository.findByUserAndStatus(user, status, pageable);
        return res.stream().map(ReservationResponse::from).toList();
    }
}
