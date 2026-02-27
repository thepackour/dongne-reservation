package com.dongne.reservation.integration;

import jakarta.transaction.Transactional;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@Transactional
@EnableJpaAuditing
class ReservationApplicationTests {

}

