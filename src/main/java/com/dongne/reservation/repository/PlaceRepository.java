package com.dongne.reservation.repository;

import com.dongne.reservation.domain.Place;

import java.util.Optional;

public interface PlaceRepository {
    Optional<Place> findById(Long id);
}
