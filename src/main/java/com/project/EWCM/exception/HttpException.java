package com.project.EWCM.exception;

import com.project.EWCM.DTO.HttpExceptionDto;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.MethodArgumentNotValidException;

@Getter
@Setter
@AllArgsConstructor
public class HttpException extends RuntimeException {
//    public UserAlreadyExistsException(String message) {
//        super(message);
//    }

    private HttpExceptionDto httpExceptionDto = new HttpExceptionDto();
    private String message;
//    private MessageErrDto messageErr;
    private int statusCode = HttpServletResponse.SC_BAD_REQUEST;

    public HttpException(int code) {
        getHttpExceptionDto().setCode(code);
    }

    public HttpException(int code, int statusCode) {
        getHttpExceptionDto().setCode(code);
        setStatusCode(statusCode);
    }

    public HttpException(int code, String message) {
        getHttpExceptionDto().setCode(code);
        setMessage(message);
    }

    public HttpException(int code, String message, int statusCode) {
        httpExceptionDto.setCode(code);
        setMessage(message);
        setStatusCode(statusCode);
    }
}
