package com.dongne.reservation.web.controller;

import com.dongne.reservation.common.response.ApiResponse;
import com.dongne.reservation.service.TimeslotService;
import com.dongne.reservation.web.dto.TimeslotResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/places/{place_id}/timeslots")
public class TimeslotController {

    private final TimeslotService timeslotService;

    public TimeslotController(TimeslotService timeslotService) {
        this.timeslotService = timeslotService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TimeslotResponse>>> getTimeslot(
            @PathVariable Long place_id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate before,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate after) {
        List<TimeslotResponse> res = timeslotService.getTimeslotsByPlaceId(place_id, before, after);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "조회에 성공했습니다.", res));
    }
}
