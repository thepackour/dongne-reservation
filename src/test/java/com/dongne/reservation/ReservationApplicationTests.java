package com.dongne.reservation;

import com.dongne.reservation.domain.User;
import com.dongne.reservation.enums.UserStatus;
import com.dongne.reservation.exception.DuplicateEmailException;
import com.dongne.reservation.repository.UserRepository;
import com.dongne.reservation.service.UserService;
import com.dongne.reservation.web.dto.SignupRequest;
import com.dongne.reservation.web.dto.SignupResponse;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@EnableJpaAuditing
class ReservationApplicationTests {

}

// mockito로 mock 객체 만들어서 unit test
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
	@Mock
	private UserRepository userRepository;

	@Mock
	private BCryptPasswordEncoder pwEncoder;

	@InjectMocks
	private UserService userService;

	@Test
	void registerSuccess() {
		SignupRequest request = new SignupRequest("asdf@gmail.com", "not_encrypted", "testuser");

		User user = User.builder()
				.id(1L)
				.email("asdf@gmail.com")
				.password("encrypted")
				.name("testuser")
				.status(UserStatus.ACTIVE)
				.reservationList(null).build();

		when(userRepository.save(any(User.class)))
				.thenReturn(user);

		SignupResponse res = userService.register(request);
		assertThat(res.getEmail()).isEqualTo(request.getEmail());
	}

	@Test
	void registerFailureByDuplicateEmail() {
		SignupRequest request = new SignupRequest("asdf@gmail.com", "not_encrypted", "testuser");

		User user = User.builder()
				.id(1L)
				.email("asdf@gmail.com")
				.password("encrypted")
				.name("testuser")
				.status(UserStatus.ACTIVE)
				.reservationList(null).build();

		User new_user = User.builder()
				.id(2L)
				.email("asdf@gmail.com")
				.password("encrypted")
				.name("testuser2")
				.status(UserStatus.ACTIVE)
				.reservationList(null).build();

		when(userRepository.findByEmail(String.valueOf("asdf@gmail.com")))
				.thenReturn(Optional.of(new_user));
		lenient().when(userRepository.save(any(User.class)))
				.thenReturn(user);

		DuplicateEmailException ex = Assertions.assertThrows(DuplicateEmailException.class, () -> {
			SignupResponse res = userService.register(request);
			System.out.println(res);
		});
		assertThat(ex.getMessage()).contains("이미 사용 중인 이메일입니다.");
	}
}

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {
	private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

	@Test
	void registerFailureByInvalidEmailFormat() {
		// DTO @Email 검증
		SignupRequest request = new SignupRequest("thisiswrongemailformat", "not_encrypted", "tsetuser");
		Set<ConstraintViolation<SignupRequest>> violations = validator.validate(request);

		assertThat(violations).isNotEmpty();
		assertThat(violations.iterator().next().getMessage())
				.contains("올바른 형식의 이메일 주소여야 합니다");
				// Hibernate Validator에서 기본 제공하는 메시지
				// ValidationMessages.properties로 바꾸려고 했는데 안됨
				// @Email(name=...) 이렇게 하면 된다고는 함
	}
}