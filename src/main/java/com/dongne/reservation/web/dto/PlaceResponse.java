package com.dongne.reservation.web.dto;

import com.dongne.reservation.domain.Place;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlaceResponse {
    private Long id;
    private String name;
    private String type;
    private Integer capacity;
    private Float rating;

    public static PlaceResponse from(Place place) {
        return PlaceResponse.builder()
                .id(place.getId())
                .name(place.getName())
                .type(place.getType())
                .capacity(place.getCapacity())
                .rating(place.getRating())
                .build();
    }
}
