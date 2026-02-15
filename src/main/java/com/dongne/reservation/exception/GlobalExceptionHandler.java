package com.dongne.reservation.exception;

import com.dongne.reservation.common.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 이메일 중복
    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ApiResponse<?>> handleDuplicateEmail(Exception e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiResponse<>(HttpStatus.CONFLICT.value(), e.getMessage()));
    }

    // @Email 검증
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleIllegalArgument(MethodArgumentNotValidException e) {
        List<String> details = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .toList();

        String message = "잘못된 요청입니다. (" + String.join(", ", details) + ")";

        return ResponseEntity.badRequest()
                .body(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), message));
    }

    // 비밀번호 오류
    @ExceptionHandler(LoginFailedException.class)
    public ResponseEntity<ApiResponse<?>> handleIllegalArgument(LoginFailedException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse<>(HttpStatus.UNAUTHORIZED.value(), e.getMessage()));
    }

    // 존재하지 않는 리소스 조회
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleNotFound(NotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(HttpStatus.NOT_FOUND.value(), e.getMessage()));
    }

    // query string 날짜 범위 오류
    @ExceptionHandler(InvalidDateTimeRangeException.class)
    public ResponseEntity<ApiResponse<?>> handleInvalidDateTimeRange(InvalidDateTimeRangeException e) {
        return ResponseEntity.badRequest()
                .body(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
    }

    // 예약 슬롯 시간 겹침
    @ExceptionHandler(DuplicateTimeslotException.class)
    public ResponseEntity<ApiResponse<?>> handleDuplicateTimeslot(DuplicateTimeslotException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiResponse<>(HttpStatus.CONFLICT.value(), e.getMessage()));
    }

    // 최대 예약 수 초과
    @ExceptionHandler(SlotCapacityExceededException.class)
    public ResponseEntity<ApiResponse<?>> handleSlotCapacityExceeded(SlotCapacityExceededException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiResponse<>(HttpStatus.CONFLICT.value(), e.getMessage()));
    }

    // 예약 변경 시 본인 확인 오류
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<?>> handleAccessDenied(AccessDeniedException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse<>(HttpStatus.UNAUTHORIZED.value(), e.getMessage()));
    }

    // 400
    @ExceptionHandler({
            IllegalArgumentException.class,
            BindException.class,
            MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class })
    public ResponseEntity<ApiResponse<?>> handleIllegalArgument(Exception e) {
        return ResponseEntity.badRequest()
                .body(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "잘못된 요청입니다."));
    }

    // 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleGeneralException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버 오류가 발생했습니다."));
    }
}
