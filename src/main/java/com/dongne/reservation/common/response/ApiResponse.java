package com.dongne.reservation.common.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private T data;
    private ErrorDetail errorDetail;
    private Meta meta;
}
