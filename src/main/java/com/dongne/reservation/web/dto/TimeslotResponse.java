package com.dongne.reservation.web.dto;

import com.dongne.reservation.domain.Place;
import com.dongne.reservation.domain.Timeslot;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class TimeslotResponse {
    @NotNull
    private Long id;

    @NotNull
    private Long placeId;

    @NotNull
    private LocalDateTime startAt;

    @NotNull
    private LocalDateTime endAt;

    @NotNull
    private Integer slotCapacity;

    @NotNull
    private Boolean isOpen;

    public static TimeslotResponse from(Timeslot timeslot) {
        return TimeslotResponse.builder()
                .id(timeslot.getId())
                .placeId(timeslot.getPlace().getId())
                .startAt(timeslot.getStartAt())
                .endAt(timeslot.getEndAt())
                .slotCapacity(timeslot.getSlotCapacity())
                .isOpen(timeslot.getIsOpen())
                .build();
    }
}
