package com.dongne.reservation.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data // 객체 간 필드 비교 때문에 넣음
public class SignupResponse {
    @NotNull
    private Long id;

    @NotBlank
    @Email
    private String email;
}
