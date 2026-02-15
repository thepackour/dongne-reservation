package com.dongne.reservation.repository;

import com.dongne.reservation.domain.User;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Primary
public interface SpringDataJpaUserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
