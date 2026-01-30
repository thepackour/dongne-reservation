package com.dongne.reservation.exception;

import com.dongne.reservation.common.response.ApiResponse;
import com.dongne.reservation.common.response.ErrorDetail;
import com.dongne.reservation.common.response.Meta;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.util.UUID;

@ControllerAdvice
public class GlobalExceptionHandler {

    // 잘못된 요청 처리
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadRequest(IllegalArgumentException ex) {
        ApiResponse<Void> response = new ApiResponse<>();
        response.setSuccess(false);
        response.setData(null);

        ErrorDetail errorDetail = new ErrorDetail();
        errorDetail.setCode("INVALID_ARGUMENT");
        errorDetail.setMessage(ex.getMessage());
        response.setErrorDetail(errorDetail);

        Meta meta = new Meta();
        meta.setTimestamp(Instant.now().toString());
        meta.setRequestId(UUID.randomUUID().toString());
        response.setMeta(meta);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // 인증/권한 문제 처리
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ApiResponse<Void>> handleForbidden(SecurityException ex) {
        ApiResponse<Void> response = new ApiResponse<>();
        response.setSuccess(false);
        response.setData(null);

        ErrorDetail errorDetail = new ErrorDetail();
        errorDetail.setCode("FORBIDDEN");
        errorDetail.setMessage("권한이 없습니다.");
        response.setErrorDetail(errorDetail);

        Meta meta = new Meta();
        meta.setTimestamp(Instant.now().toString());
        meta.setRequestId(UUID.randomUUID().toString());
        response.setMeta(meta);

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    // 서버 내부 오류 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleServerError(Exception ex) {
        ApiResponse<Void> response = new ApiResponse<>();
        response.setSuccess(false);
        response.setData(null);

        ErrorDetail errorDetail = new ErrorDetail();
        errorDetail.setCode("INTERNAL_SERVER_ERROR");
        errorDetail.setMessage("서버 내부 오류가 발생했습니다.");
        response.setErrorDetail(errorDetail);

        Meta meta = new Meta();
        meta.setTimestamp(Instant.now().toString());
        meta.setRequestId(UUID.randomUUID().toString());
        response.setMeta(meta);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
