package com.dongne.reservation.domain;

import com.dongne.reservation.domain.common.BaseEntity;
import com.dongne.reservation.enums.PlaceStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Place extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String type;

    @NotNull
    @Enumerated(EnumType.STRING)
    private PlaceStatus status;

    @NotNull
    private Integer capacity;

    @OneToMany(mappedBy = "place", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Timeslot> timeslotList;

    @NotNull
    private Float rating;

}
