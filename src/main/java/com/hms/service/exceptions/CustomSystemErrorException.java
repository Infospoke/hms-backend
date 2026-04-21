package com.hms.service.exceptions;

public class CustomSystemErrorException extends RuntimeException{
    public CustomSystemErrorException(String message){
        super(message);
    }
    public CustomSystemErrorException(String message, Throwable cause){
        super(message, cause);
    }
}

