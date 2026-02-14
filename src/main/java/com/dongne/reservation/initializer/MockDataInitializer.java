package com.dongne.reservation.initializer;

import com.dongne.reservation.domain.Place;
import com.dongne.reservation.domain.Reservation;
import com.dongne.reservation.domain.Timeslot;
import com.dongne.reservation.domain.User;
import com.dongne.reservation.enums.PlaceStatus;
import com.dongne.reservation.enums.ReservationStatus;
import com.dongne.reservation.enums.UserRole;
import com.dongne.reservation.enums.UserStatus;
import com.dongne.reservation.repository.SpringDataJpaPlaceRepository;
import com.dongne.reservation.repository.SpringDataJpaReservationRepository;
import com.dongne.reservation.repository.SpringDataJpaTimeslotRepository;
import com.dongne.reservation.repository.SpringDataJpaUserRepository;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;

@Component
public class MockDataInitializer implements CommandLineRunner {

    private final SpringDataJpaUserRepository springDataJpaUserRepository;
    private final SpringDataJpaPlaceRepository springDataJpaPlaceRepository;
    private final SpringDataJpaTimeslotRepository springDataJpaTimeslotRepository;
    private final SpringDataJpaReservationRepository springDataJpaReservationRepository;
    private final BCryptPasswordEncoder pwEncoder;

    public MockDataInitializer(SpringDataJpaUserRepository springDataJpaUserRepository,
                               SpringDataJpaPlaceRepository springDataJpaPlaceRepository,
                               SpringDataJpaTimeslotRepository springDataJpaTimeslotRepository,
                               SpringDataJpaReservationRepository springDataJpaReservationRepository,
                               BCryptPasswordEncoder pwEncoder) {
        this.springDataJpaUserRepository = springDataJpaUserRepository;
        this.springDataJpaPlaceRepository = springDataJpaPlaceRepository;
        this.springDataJpaTimeslotRepository = springDataJpaTimeslotRepository;
        this.springDataJpaReservationRepository = springDataJpaReservationRepository;
        this.pwEncoder = pwEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // user
        UserData[] userDataArr = {
                new UserData("admin@gmail.com", "therealadmin", "admin_user", UserStatus.ACTIVE, UserRole.ADMIN),
                new UserData("dongne@gmail.com", "CCSSAA", "dongne_user", UserStatus.ACTIVE, UserRole.USER),
                new UserData("example@gmail.com", "verynicepw", "random_user", UserStatus.ACTIVE, UserRole.USER),
                new UserData("asdf@gmail.com", "normalpw", "suspended_user", UserStatus.SUSPENDED, UserRole.USER) };
        for (UserData userData : userDataArr) {
            User user = User.builder()
                    .email(userData.email())
                    .password(pwEncoder.encode(userData.password()))
                    .name(userData.name())
                    .status(userData.status())
                    .reservationList(List.of())
                    .role(userData.role())
                    .build();
            springDataJpaUserRepository.save(user);
        }

        // place
        for (int i = 0; i < 16; i++) {
            Place place = Place.builder()
                    .name(String.valueOf('A' + i))
                    .type("type_" + ('a' + i))
                    .status(Math.random() > 0.5 ? PlaceStatus.OPEN : PlaceStatus.CLOSED)
                    .capacity((int) (Math.random() * 3 + 1))
                    .rating((float) (Math.random() * 5))
                    .rsvCount(0)
                    .build();
            springDataJpaPlaceRepository.save(place);
        }

        // timeslot
        for (int i = 0; i < 16; i++) {
            int day = LocalDateTime.now().getDayOfMonth() + (int) (Math.random() * 14);
            int hour = (int) (Math.random() * 20);
            int min = Math.random() > 0.5 ? 0 : 30;
            Timeslot timeslot1 = Timeslot.builder()
                    .place(springDataJpaPlaceRepository.findById((long) (i + 1)).orElse(null))
                    .startAt(LocalDateTime.of(2026, 2, day, hour, min))
                    .endAt(LocalDateTime.of(2026, 2, day, hour + 2, min))
                    .slotCapacity((int) (Math.random() * 4))
                    .isOpen(Math.random() > 0.5)
                    .build();
            Timeslot timeslot2 = Timeslot.builder()
                    .place(springDataJpaPlaceRepository.findById((long) (i + 1)).orElse(null))
                    .startAt(LocalDateTime.of(2026, 2, day, hour + 2, min))
                    .endAt(LocalDateTime.of(2026, 2, day, hour + 4, min))
                    .slotCapacity((int) (Math.random() * 4))
                    .isOpen(Math.random() > 0.5)
                    .build();
            springDataJpaTimeslotRepository.save(timeslot1);
            springDataJpaTimeslotRepository.save(timeslot2);
        }

        // reservation
        Set<Integer> timeslotSet = new HashSet<>();
        while (timeslotSet.size() < 3) {
            int num = (int) (Math.random() * 32 + 1); // 1~10
            timeslotSet.add(num);
        }
        Iterator<Integer> iterator = timeslotSet.iterator();
        for (int i = 0; i < 3; i++) {
            Timeslot timeslot = springDataJpaTimeslotRepository.findById(iterator.next()).orElse(null);
            Reservation reservation = Reservation.builder()
                    .user(springDataJpaUserRepository.findById(3L).orElse(null))
                    .timeslot(timeslot)
                    .status(ReservationStatus.values()[(int)(Math.random() * 3)])
                    .build();
            springDataJpaReservationRepository.save(reservation);
            timeslot.getPlace().increaseRsvCount();
        }
        iterator.remove();

        System.out.println("Mock data initialized!");
    }

    private record UserData(
            String email,
            String password,
            String name,
            UserStatus status,
            UserRole role
    ) { }
}
