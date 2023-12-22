package com.interswitch.inventoryservice.exception;

public class MicroserviceException extends RuntimeException {

    public MicroserviceException(String message, Exception ex) {
        super(message,ex);
    }
}
