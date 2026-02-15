package com.dongne.reservation.service;

import com.dongne.reservation.domain.Timeslot;
import com.dongne.reservation.exception.InvalidDateTimeRangeException;
import com.dongne.reservation.exception.NotFoundException;
import com.dongne.reservation.repository.SpringDataJpaPlaceRepository;
import com.dongne.reservation.repository.SpringDataJpaTimeslotRepository;
import com.dongne.reservation.web.dto.TimeslotResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequestMapping("places/{place_id}/timeslots")
public class TimeslotService {

    private final SpringDataJpaTimeslotRepository springDataJpaTimeslotRepository;
    private final SpringDataJpaPlaceRepository springDataJpaPlaceRepository;

    public TimeslotService(SpringDataJpaTimeslotRepository springDataJpaTimeslotRepository,
                           SpringDataJpaPlaceRepository springDataJpaPlaceRepository) {
        this.springDataJpaTimeslotRepository = springDataJpaTimeslotRepository;
        this.springDataJpaPlaceRepository = springDataJpaPlaceRepository;
    }

    public List<TimeslotResponse> getTimeslotsByPlaceId(Long id, LocalDate before, LocalDate after) {
        if (!existsPlaceById(id)) throw new NotFoundException("장소를 찾을 수 없습니다.");

        List<Timeslot> timeslotList;
        if (before != null && after != null) {
            if (after.isAfter(before)) throw new InvalidDateTimeRangeException("query parameter 'before'은 'after' 이전이어야 합니다.");
            timeslotList = springDataJpaTimeslotRepository.findByPlaceIdAndStartAtBetween(id, before.atStartOfDay(), after.atTime(23,59,59));
        } else if (before != null) {
            timeslotList = springDataJpaTimeslotRepository.findByPlaceIdAndStartAtLessThanEqual(id, before.atStartOfDay());
        } else if (after != null) {
            timeslotList = springDataJpaTimeslotRepository.findByPlaceIdAndStartAtGreaterThanEqual(id, after.atStartOfDay());
        } else {
            timeslotList = springDataJpaTimeslotRepository.findByPlaceId(id);
        }

        return timeslotList.stream()
                .map(TimeslotResponse::from)
                .collect(Collectors.toList());
    }

    private boolean existsPlaceById(Long id) {
        return springDataJpaPlaceRepository.existsById(id);
    }
}
