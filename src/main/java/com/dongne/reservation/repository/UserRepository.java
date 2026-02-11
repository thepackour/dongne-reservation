package com.dongne.reservation.repository;

import com.dongne.reservation.domain.User;
import com.dongne.reservation.web.dto.SignupRequest;

import java.util.Optional;

public interface UserRepository {
    User save(User user);
    Optional<User> findOne(Long id);
    Optional<User> findByEmail(String email);
}
