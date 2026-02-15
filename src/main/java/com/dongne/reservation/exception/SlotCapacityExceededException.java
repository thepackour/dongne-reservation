package com.dongne.reservation.exception;

public class SlotCapacityExceededException extends RuntimeException {
    public SlotCapacityExceededException(String message) {
        super(message);
    }
}
