package com.dongne.reservation.domain;

import com.dongne.reservation.domain.common.BaseEntity;
import com.dongne.reservation.enums.ReservationStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class Reservation extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne
    @JoinColumn(name = "timeslot")
    private Timeslot timeslot;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    // Timeslot에 place 정보가 있으므로 place 필드를 굳이 쓸 필요는 없는듯
    // 오히려 place 필드가 있으면 place 정보가 변경되었을 때 참조하는 모든 reservation의 필드를 변경해야 함
    public String getPlaceName() { return timeslot.getPlace().getName(); }

}
