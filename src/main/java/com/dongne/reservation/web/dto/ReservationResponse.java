package com.dongne.reservation.web.dto;

import com.dongne.reservation.domain.Reservation;
import com.dongne.reservation.domain.Timeslot;
import com.dongne.reservation.domain.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ReservationResponse {

    // Reservation 정보
    @NotNull
    private Long id;

    @NotBlank
    private String status;

    @NotNull
    private LocalDateTime createdAt;


    // User 정보
    @NotNull
    private Long userId;

    @NotBlank
    private String userName;

    @NotBlank
    @Email
    private String email;


    // Timeslot 정보
    @NotNull
    private Long timeslotId;

    @NotNull
    private LocalDateTime startAt;

    @NotNull
    private LocalDateTime endAt;


    // Place 정보
    @NotNull
    private Long placeId;

    @NotBlank
    private String placeName;


    public static ReservationResponse from(Reservation reservation) {
        User user = reservation.getUser();
        Timeslot timeslot = reservation.getTimeslot();
        return ReservationResponse.builder()
                .id(reservation.getId())
                .status(reservation.getStatus().name())
                .createdAt(reservation.getCreatedAt())
                .userId(user.getId())
                .userName(user.getName())
                .email(user.getEmail())
                .timeslotId(timeslot.getId())
                .startAt(timeslot.getStartAt())
                .endAt(timeslot.getEndAt())
                .placeId(reservation.getPlaceId())
                .placeName(reservation.getPlaceName())
                .build();
    }
}
