package com.demo.shopapp.shared.exceptions;

public class InvalidParamException extends IllegalArgumentException {
    public InvalidParamException(String message) {
        super(message);
    }
}
