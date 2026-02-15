package com.dongne.reservation.exception;

public class DuplicateTimeslotException extends RuntimeException {
    public DuplicateTimeslotException(String message) {
        super(message);
    }
}
