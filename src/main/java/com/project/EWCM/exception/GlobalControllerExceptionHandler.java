package com.project.EWCM.exception;

import com.project.EWCM.DTO.HttpExceptionDto;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalControllerExceptionHandler {
    public static int ENTITY_NOT_FOUND_EXCEPTION_CODE = 10048;


    protected Logger logger;

    public GlobalControllerExceptionHandler() {
        logger = LoggerFactory.getLogger(getClass());
    }

    @ExceptionHandler(HttpException.class)
    @ResponseBody
    public HttpExceptionDto handleHttpException(HttpServletResponse response, HttpException e) {
        response.setStatus(e.getStatusCode());
        e.getHttpExceptionDto().bindMessageSource(e.getMessage());
        logger.warn(e.getHttpExceptionDto().toString());
        return e.getHttpExceptionDto();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}
