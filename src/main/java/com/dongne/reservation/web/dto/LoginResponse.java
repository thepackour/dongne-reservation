package com.dongne.reservation.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data // 객체 간 필드 비교 때문에 넣음
public class LoginResponse {
    @NotEmpty
    private String token;

    @NotNull
    private Long id;

    @NotBlank
    private String email;

    @NotBlank
    private String name;

}
