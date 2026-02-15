package com.dongne.reservation.web.controller;

import com.dongne.reservation.common.response.ApiResponse;
import com.dongne.reservation.enums.ReservationStatus;
import com.dongne.reservation.service.ReservationService;
import com.dongne.reservation.web.dto.ReservationResponse;
import com.dongne.reservation.web.dto.UpdateReservationRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/places/{place_id}/timeslots/{timeslot_id}/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ReservationResponse>> createReservation(
        @PathVariable Long place_id,
        @PathVariable Long timeslot_id,
        HttpServletRequest request
    ) {
        String token = request.getHeader("Authorization").substring(7);
        ReservationResponse res = reservationService.createReservation(place_id, timeslot_id, token);
        URI location = URI.create("/places/" + res.getPlaceId() + "/timeslots/" + res.getTimeslotId() + "/reservations/" + res.getId());
        return ResponseEntity.created(location)
                    .body(new ApiResponse<>(HttpStatus.CREATED.value(), "예약이 성공적으로 생성되었습니다.", res));
    }

    @PatchMapping("/{reservation_id}")
    public ResponseEntity<ApiResponse<ReservationResponse>> updateReservation(
            @PathVariable Long place_id,
            @PathVariable Long timeslot_id,
            @PathVariable Long reservation_id,
            @RequestBody UpdateReservationRequest updateReservationRequest,
            HttpServletRequest request
    ) {
        String token = request.getHeader("Authorization").substring(7);
        ReservationResponse res = reservationService.updateReservation(
                place_id, timeslot_id, reservation_id, updateReservationRequest, token);

        return ResponseEntity.ok()
                .body(new ApiResponse<>(HttpStatus.OK.value(), "성공적으로 변경되었습니다.", res));
    }
}
