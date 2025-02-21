package com.demo.shopapp.exceptions;

public class InvalidParamException extends IllegalArgumentException {
    public InvalidParamException(String message) {
        super(message);
    }
}
