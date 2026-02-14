package com.dongne.reservation.web.controller;

import com.dongne.reservation.common.response.ApiResponse;
import com.dongne.reservation.service.UserService;
import com.dongne.reservation.web.dto.SignupRequest;
import com.dongne.reservation.web.dto.SignupResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) { this.userService = userService; }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignupResponse>> signup(@Valid @RequestBody SignupRequest request) {
        SignupResponse res = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(HttpStatus.CREATED.value(), "회원가입이 성공적으로 완료되었습니다.", res));
    }
}
