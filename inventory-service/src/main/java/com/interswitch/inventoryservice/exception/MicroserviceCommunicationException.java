package com.interswitch.inventoryservice.exception;

public class MicroserviceCommunicationException extends RuntimeException {

    public MicroserviceCommunicationException(String message, Throwable cause) {
        super(message, cause);
    }

    public MicroserviceCommunicationException(String message) {
        super(message);
    }
}

