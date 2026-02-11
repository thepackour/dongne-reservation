package com.dongne.reservation.domain;

import com.dongne.reservation.domain.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Timeslot extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", nullable = false)
    private Place place;

    @NotNull
    @Column(name = "start_at")
    private LocalDateTime startAt;

    @NotNull
    @Column(name = "end_at")
    private LocalDateTime endAt;

    @NotNull
    @Column(name = "slot_capacity")
    private Integer slotCapacity;

    @Column(name = "is_open")
    private Boolean isOpen;

    @OneToOne(mappedBy = "timeslot", cascade = CascadeType.ALL)
    private Reservation reservation;

}
