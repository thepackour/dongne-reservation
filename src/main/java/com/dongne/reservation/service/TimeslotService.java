package com.dongne.reservation.service;

import com.dongne.reservation.domain.Timeslot;
import com.dongne.reservation.exception.DuplicateTimeslotException;
import com.dongne.reservation.exception.InvalidDateTimeRangeException;
import com.dongne.reservation.exception.NotFoundException;
import com.dongne.reservation.repository.SpringDataJpaPlaceRepository;
import com.dongne.reservation.repository.SpringDataJpaTimeslotRepository;
import com.dongne.reservation.web.dto.TimeslotRequest;
import com.dongne.reservation.web.dto.TimeslotResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    @Transactional(readOnly = true)
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

    @Transactional
    public TimeslotResponse createTimeslot(Long id, TimeslotRequest request) {
        // 예외 처리 (존재하지 않는 장소, 시간 겹침, 최대 예약 인원 범위 초과)
        if (!existsPlaceById(id)) throw new NotFoundException("장소를 찾을 수 없습니다.");
        if (request.getStartAt().isAfter(request.getEndAt()))
            throw new InvalidDateTimeRangeException("'startAt'은 'endAt' 이전이어야 합니다.");
        if (springDataJpaTimeslotRepository
                .existsTimeslotByPlaceIdAndEndAtGreaterThanEqualAndStartAtLessThan(id, request.getStartAt(), request.getEndAt()))
            throw new DuplicateTimeslotException("시간이 겹치는 예약이 존재합니다.");
        int slotCapacity = request.getSlotCapacity();
        if (slotCapacity < 1) throw new IllegalArgumentException("'slotCapacity'는 1 이상이어야 합니다.");

        // 객체 생성 및 저장
        Timeslot timeslot = Timeslot.builder()
                .place(springDataJpaPlaceRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("장소를 찾을 수 없습니다.")))
                .startAt(request.getStartAt())
                .endAt(request.getEndAt())
                .slotCapacity(request.getSlotCapacity())
                .isOpen(false)
                .build();
        Timeslot res = springDataJpaTimeslotRepository.save(timeslot);
        return TimeslotResponse.from(res);
    }

    private boolean existsPlaceById(Long id) {
        return springDataJpaPlaceRepository.existsById(id);
    }
}
