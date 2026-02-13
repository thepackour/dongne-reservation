package com.dongne.reservation.common.response;

import lombok.*;

@Getter
public class ApiResponse<T> {
    private final int status;
    private final String message;
    private final T data;

    public ApiResponse(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public ApiResponse(int status, String message) {
        this(status, message, null);
    }
}
