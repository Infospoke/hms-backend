package com.hms.service.wrappers;

import lombok.Getter;

@Getter
public enum ResponseCode {
    SUCCESS("00", "Success"),
    FAILURE("01", "Failure");

    private final String code;
    private final String message;

    ResponseCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

}
