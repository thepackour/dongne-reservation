package com.dongne.reservation.unit;

import com.dongne.reservation.domain.Place;
import com.dongne.reservation.enums.PlaceStatus;
import com.dongne.reservation.exception.NotFoundException;
import com.dongne.reservation.repository.SpringDataJpaPlaceRepository;
import com.dongne.reservation.service.PlaceService;
import com.dongne.reservation.web.dto.PlaceResponse;
import com.dongne.reservation.web.dto.SignupRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlaceServiceTest {

    @Mock
    private SpringDataJpaPlaceRepository placeRepository;

    @InjectMocks
    private PlaceService placeService;

    @Test
    void getPlacesSuccess() {
        when(placeRepository.findAll(any(Pageable.class)))
                .thenReturn(Page.empty());

        assertThat(placeService.getPlaces(Pageable.ofSize(5)))
                .isInstanceOf(Page.class);
    }

    @Test
    void getPlaceByIdSuccess() {
        Optional<Place> optPlace = getMockPlaces().stream().findFirst();

        when(placeRepository.findById(1L))
                .thenReturn(optPlace);

        PlaceResponse answer = PlaceResponse.from(optPlace.get());

        assertEquals(placeService.getPlaceById(1L), answer);
    }

    @Test
    void getPlaceByIdNotFound() {
        when(placeRepository.findById(any()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            PlaceResponse res = placeService.getPlaceById(99L);
            System.out.println(res.toString());
        });
    }


    private List<Place> getMockPlaces() {
        List<Place> places = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            Place place = Place.builder()
                    .name(String.valueOf((char) ('A' + i)))
                    .type("type_" + (char) ('a' + i))
                    .status(Math.random() > 0.5 ? PlaceStatus.OPEN : PlaceStatus.CLOSED)
                    .capacity((int) (Math.random() * 3 + 1))
                    .rating((float) (Math.random() * 5))
                    .rsvCount(0)
                    .build();
            places.add(place);
        }
        return places;
    }
}
