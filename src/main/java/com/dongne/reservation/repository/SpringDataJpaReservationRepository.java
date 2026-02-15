package com.dongne.reservation.repository;

import com.dongne.reservation.domain.Reservation;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Primary
public interface SpringDataJpaReservationRepository extends JpaRepository<Reservation, Long> {

}
