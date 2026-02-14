package com.dongne.reservation.web.controller;

import com.dongne.reservation.common.response.ApiResponse;
import com.dongne.reservation.service.PlaceService;
import com.dongne.reservation.web.dto.PlaceResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/places")
public class PlaceController {

    private final PlaceService placeService;

    public PlaceController(PlaceService placeService) {
        this.placeService = placeService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<PlaceResponse>>> getPlaces(Pageable pageable) {
        // pageable 쓰면 @RequestParam 없이 query parameter 받을 수 있다고 함
        Page<PlaceResponse> result = placeService.getPlaces(pageable);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "조회에 성공했습니다.", result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PlaceResponse>> getPlace(@PathVariable long id) {
        PlaceResponse res = placeService.getPlaceById(id);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "조회에 성공했습니다.", res));
    }
}
