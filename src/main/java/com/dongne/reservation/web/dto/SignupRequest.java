package com.dongne.reservation.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {
    @NotBlank
    @Email  // 이메일 형식인지 검증하는 어노테이션
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    private String name;
}
