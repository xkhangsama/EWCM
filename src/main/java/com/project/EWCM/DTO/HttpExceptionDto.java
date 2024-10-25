package com.project.EWCM.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HttpExceptionDto implements Serializable {
    public static String MESSAGE_SOURCE_PREFIX = "vn.project.EWCM.http.response.error.";

    private int code;
    private String message;

    public HttpExceptionDto(int code) {
        setCode(code);
    }

    public HttpExceptionDto bindMessageSource() {
        setMessage(MESSAGE_SOURCE_PREFIX + getCode());
        return this;
    }

    public HttpExceptionDto bindMessageSource(String message) {
        setMessage(MESSAGE_SOURCE_PREFIX + getCode() + message);
        return this;
    }

    @Override
    public String toString() {
        return "DIGO-" + getCode() + ": " + getMessage();
    }
}
