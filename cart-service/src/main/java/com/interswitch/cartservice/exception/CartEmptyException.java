package com.interswitch.cartservice.exception;

public class CartEmptyException extends RuntimeException{

    public CartEmptyException(String message) {
        super(message);
    }
}
