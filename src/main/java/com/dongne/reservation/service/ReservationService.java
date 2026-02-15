package com.dongne.reservation.service;

import com.dongne.reservation.domain.Place;
import com.dongne.reservation.domain.Reservation;
import com.dongne.reservation.domain.Timeslot;
import com.dongne.reservation.domain.User;
import com.dongne.reservation.enums.ReservationStatus;
import com.dongne.reservation.exception.InvalidDateTimeRangeException;
import com.dongne.reservation.exception.NotFoundException;
import com.dongne.reservation.exception.SlotCapacityExceededException;
import com.dongne.reservation.jwt.JwtTokenProvider;
import com.dongne.reservation.repository.SpringDataJpaPlaceRepository;
import com.dongne.reservation.repository.SpringDataJpaReservationRepository;
import com.dongne.reservation.repository.SpringDataJpaTimeslotRepository;
import com.dongne.reservation.repository.SpringDataJpaUserRepository;
import com.dongne.reservation.web.dto.ReservationResponse;
import com.dongne.reservation.web.dto.UpdateReservationRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ReservationService {

    private final SpringDataJpaUserRepository springDataJpaUserRepository;
    private final SpringDataJpaPlaceRepository springDataJpaPlaceRepository;
    private final SpringDataJpaTimeslotRepository springDataJpaTimeslotRepository;
    private final SpringDataJpaReservationRepository springDataJpaReservationRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public ReservationService(SpringDataJpaUserRepository springDataJpaUserRepository,
                              SpringDataJpaPlaceRepository springDataJpaPlaceRepository,
                              SpringDataJpaTimeslotRepository springDataJpaTimeslotRepository,
                              SpringDataJpaReservationRepository springDataJpaReservationRepository,
                              JwtTokenProvider jwtTokenProvider) {
        this.springDataJpaUserRepository = springDataJpaUserRepository;
        this.springDataJpaPlaceRepository = springDataJpaPlaceRepository;
        this.springDataJpaTimeslotRepository = springDataJpaTimeslotRepository;
        this.springDataJpaReservationRepository = springDataJpaReservationRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Transactional
    public ReservationResponse createReservation(Long placeId,
                                                 Long timeslotId,
                                                 String token) {
        // 예외 처리 (존재하지 않는 장소)
        if (!existsPlaceById(placeId))
            throw new NotFoundException("장소를 찾을 수 없습니다.");

        // 예외 처리 (존재하지 않는 예약 슬롯)
        Timeslot timeslot = springDataJpaTimeslotRepository.findById(timeslotId)
                .orElseThrow(() -> new NotFoundException("예약 슬롯을 찾을 수 없습니다."));

        // 예외 처리 (다른 장소를 참조하는 예약 슬롯)
        if (!timeslot.getPlace().getId().equals(placeId))
            throw new NotFoundException("해당 장소에 속하지 않는 예약 슬롯입니다.");

        // 예외 처리 (존재하지 않는 유저)
        String email = jwtTokenProvider.getUsername(token);
        User user = springDataJpaUserRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("유저를 찾을 수 없습니다."));

        // 예외 처리 (최대 예약 가능 수 초과)
        if (timeslot.getReservationList().size() >= timeslot.getSlotCapacity())
            throw new SlotCapacityExceededException("예약 한도를 초과했습니다.");

        // 객체 생성 및 저장
        Reservation reservation = Reservation.builder()
                .user(user)
                .timeslot(timeslot)
                .status(ReservationStatus.CONFIRMED)
                .build();
        springDataJpaReservationRepository.save(reservation);
        timeslot.getPlace().increaseRsvCount();

        return ReservationResponse.from(reservation);
    }

    @Transactional
    public ReservationResponse updateReservation(Long placeId,
                                  Long timeslotId,
                                  Long reservationId,
                                  UpdateReservationRequest request,
                                  String token) {
        // 예외 처리 (존재하지 않는 장소)
        if (!existsPlaceById(placeId))
            throw new NotFoundException("장소를 찾을 수 없습니다.");

        // 예외 처리 (존재하지 않는 예약 슬롯)
        Timeslot timeslot = springDataJpaTimeslotRepository.findById(timeslotId)
                .orElseThrow(() -> new NotFoundException("예약 슬롯을 찾을 수 없습니다."));

        Reservation reservation = springDataJpaReservationRepository.findById(reservationId)
                .orElseThrow(() -> new NotFoundException("예약을 찾을 수 없습니다."));

        // 예외 처리 (다른 장소를 참조하는 예약 슬롯)
        if (!timeslot.getPlace().getId().equals(placeId))
            throw new NotFoundException("해당 장소에 속하지 않는 예약 슬롯입니다.");

        // 예외 처리 (다른 예약 슬롯을 참조하는 예약)
        if (!reservation.getTimeslot().getId().equals(timeslotId))
            throw new NotFoundException("해당 예약 슬롯에 속하지 않는 예약입니다.");

        // 예외 처리 (본인 확인 실패)
        String email = jwtTokenProvider.getUsername(token);
        if (!email.equals(reservation.getUser().getEmail()))
            throw new AccessDeniedException("본인의 예약만 취소할 수 있습니다.");


        // status 수정 후 저장
        reservation.setStatus(ReservationStatus.valueOf(request.getStatus()));
        Reservation res = springDataJpaReservationRepository.save(reservation);

        return ReservationResponse.from(res);
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> getReservationsByPlace(
            Long placeId, LocalDate before, LocalDate after) {
        Place place = springDataJpaPlaceRepository.findById(placeId)
                .orElseThrow(() -> new NotFoundException("장소를 찾을 수 없습니다."));

        List<Reservation> rsvList = place.getTimeslotList().stream()
                .flatMap(timeslot -> timeslot.getReservationList().stream())
                .collect(Collectors.toList());
        List<Reservation> filteredRsvList;
        if (before != null && after != null) {
            if (after.isAfter(before)) throw new InvalidDateTimeRangeException("query parameter 'before'은 'after' 이전이어야 합니다.");
            filteredRsvList = rsvList.stream()
                    .filter(rsv -> {
                        LocalDateTime startAt = rsv.getStartAt();
                        boolean cond1 = startAt.isBefore(before.atTime(23,59,59));
                        boolean cond2 = startAt.isAfter(after.atStartOfDay());
                        return cond1 && cond2; })
                    .collect(Collectors.toList());
        } else if (before != null) {
            filteredRsvList = rsvList.stream()
                    .filter(rsv -> rsv.getStartAt().isBefore(before.atTime(23,59,59)) )
                    .collect(Collectors.toList());
        } else if (after != null) {
            filteredRsvList = rsvList.stream()
                    .filter(rsv -> rsv.getStartAt().isAfter(after.atStartOfDay()) )
                    .collect(Collectors.toList());
        } else filteredRsvList = rsvList;
        return filteredRsvList.stream()
                .map(ReservationResponse::from)
                .collect(Collectors.toList());
    }

    private boolean existsPlaceById(Long placeId) {
        return springDataJpaPlaceRepository.existsById(placeId);
    }
}
