package com.dongne.reservation.repository;

import com.dongne.reservation.domain.Reservation;
import com.dongne.reservation.domain.User;
import com.dongne.reservation.enums.ReservationStatus;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Primary
public interface SpringDataJpaReservationRepository extends JpaRepository<Reservation, Long> {
    Page<Reservation> findByUser(User user, Pageable pageable);
    Page<Reservation> findByUserAndStatus(User user, ReservationStatus status, Pageable pageable);
}
