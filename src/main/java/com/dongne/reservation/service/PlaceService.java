package com.dongne.reservation.service;

import com.dongne.reservation.domain.Place;
import com.dongne.reservation.exception.NotFoundException;
import com.dongne.reservation.repository.JpaPlaceRepository;
import com.dongne.reservation.repository.SpringDataJpaPlaceRepository;
import com.dongne.reservation.web.dto.PlaceResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PlaceService {

    private final SpringDataJpaPlaceRepository springDataJpaPlaceRepository;

    public PlaceService(SpringDataJpaPlaceRepository springDataJpaPlaceRepository) {
        this.springDataJpaPlaceRepository = springDataJpaPlaceRepository;
    }

    @Transactional(readOnly = true)
    public Page<PlaceResponse> getPlaces(Pageable pageable) {
        return springDataJpaPlaceRepository.findAll(pageable)
                .map(PlaceResponse::from);
    }

    @Transactional(readOnly = true)
    public PlaceResponse getPlaceById(Long id) {
        Place res = springDataJpaPlaceRepository.findById(id).orElseThrow(() -> new NotFoundException("장소 조회에 실패했습니다."));
        return PlaceResponse.from(res);
    }
}
