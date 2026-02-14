package com.dongne.reservation.repository;

import com.dongne.reservation.domain.Place;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Transactional
public class JpaPlaceRepository implements PlaceRepository {

    private final EntityManager em;

    public JpaPlaceRepository(EntityManager em) { this.em = em; }

    @Override
    public Optional<Place> findById(Long id) { return Optional.of(em.find(Place.class, id)); }

}
