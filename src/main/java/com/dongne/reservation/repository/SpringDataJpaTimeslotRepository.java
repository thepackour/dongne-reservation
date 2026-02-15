package com.dongne.reservation.repository;

import com.dongne.reservation.domain.Timeslot;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Primary
@Transactional
public interface SpringDataJpaTimeslotRepository extends JpaRepository<Timeslot, Integer> {

}
