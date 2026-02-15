package com.dongne.reservation.repository;

import com.dongne.reservation.domain.Timeslot;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@Primary
@Transactional
public interface SpringDataJpaTimeslotRepository extends JpaRepository<Timeslot, Integer> {
    List<Timeslot> findByPlaceIdAndStartAtBetween(Long placeId, LocalDateTime before, LocalDateTime after);
    List<Timeslot> findByPlaceIdAndStartAtGreaterThanEqual(Long placeId, LocalDateTime after);
    List<Timeslot> findByPlaceIdAndStartAtLessThanEqual(Long placeId, LocalDateTime before);
    List<Timeslot> findByPlaceId(Long placeId);
    boolean existsTimeslotByPlaceIdAndEndAtGreaterThanEqualAndStartAtLessThan(Long placeId, LocalDateTime startAt, LocalDateTime endAt);
}
