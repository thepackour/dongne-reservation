package com.dongne.reservation.service;

import com.dongne.reservation.repository.JpaPlaceRepository;
import com.dongne.reservation.repository.SpringDataJpaPlaceRepository;
import com.dongne.reservation.web.dto.PlaceResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class PlaceService {

    private final SpringDataJpaPlaceRepository springDataJpaPlaceRepository;
    private final JpaPlaceRepository jpaPlaceRepository;

    public PlaceService(SpringDataJpaPlaceRepository springDataJpaPlaceRepository,
                        JpaPlaceRepository jpaPlaceRepository) {
        this.springDataJpaPlaceRepository = springDataJpaPlaceRepository;
        this.jpaPlaceRepository = jpaPlaceRepository;
    }

    public Page<PlaceResponse> getPlaces(Pageable pageable) {
        return springDataJpaPlaceRepository.findAll(pageable)
                .map(PlaceResponse::from);
    }
}
