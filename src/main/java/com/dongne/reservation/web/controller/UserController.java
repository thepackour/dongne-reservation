package com.dongne.reservation.web.controller;

import com.dongne.reservation.common.response.ApiResponse;
import com.dongne.reservation.enums.ReservationStatus;
import com.dongne.reservation.service.ReservationService;
import com.dongne.reservation.service.UserService;
import com.dongne.reservation.web.dto.ReservationResponse;
import com.dongne.reservation.web.dto.UpdateReservationRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users/me")
public class UserController {

    private final UserService userService;
    private final ReservationService reservationService;

    public UserController(UserService userService,
                          ReservationService reservationService) {
        this.userService = userService;
        this.reservationService = reservationService;
    }

    @GetMapping("/reservations")
    public ResponseEntity<ApiResponse<List<ReservationResponse>>> getAllReservations(
            Pageable pageable,
            @RequestParam(required = false) ReservationStatus status, // enum 값이 아닌 경우 예외 발생
            @RequestHeader("Authorization") String token) {
        List<ReservationResponse> res = userService.getMyReservations(pageable, status, token.substring(7));
        return ResponseEntity.ok()
                .body(new ApiResponse<>(HttpStatus.OK.value(), "조회에 성공했습니다.", res));

    }

    @PatchMapping("/reservations/{reservation_id}")
    public ResponseEntity<ApiResponse<ReservationResponse>> updateReservation(
            @PathVariable Long reservation_id,
            @RequestBody UpdateReservationRequest updateReservationRequest,
            HttpServletRequest request
    ) {
        String token = request.getHeader("Authorization").substring(7);
        ReservationResponse res = reservationService.updateReservation(
                reservation_id, updateReservationRequest, token);

        return ResponseEntity.ok()
                .body(new ApiResponse<>(HttpStatus.OK.value(), "성공적으로 변경되었습니다.", res));
    }
}
