package com.web_tracking.configException;

import com.bellelanco_api.constants.WebConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.Map;
import java.util.Objects;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGlobalException(Exception ex) {
        logger.error(ex.getMessage());
        return new ResponseEntity<>(
                Map.of(
                        WebConstants.STATUS,WebConstants.FAIL,
                        WebConstants.MESSAGE,WebConstants.UNKNOWN_ERROR
                ),
                HttpStatus.INTERNAL_SERVER_ERROR
        );

    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Object> handleGlobalException(MissingServletRequestParameterException ex) {
        logger.error(Objects.requireNonNull(ex.getMessage()));
        return new ResponseEntity<>(
                Map.of(
                        WebConstants.STATUS,WebConstants.FAIL,
                        WebConstants.MESSAGE, WebConstants.UNKNOWN_ERROR
                ),
                HttpStatus.BAD_REQUEST
        );

    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Object> handleMethodArgumentTypeMismatchException(HttpRequestMethodNotSupportedException ex) {
        logger.error(ex.getMessage());
        return new ResponseEntity<>(
                Map.of(
                        WebConstants.STATUS, WebConstants.FAIL,
                        WebConstants.MESSAGE, WebConstants.UNKNOWN_ERROR
                ),
                HttpStatus.METHOD_NOT_ALLOWED
        );

    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Object> handle404Exception(NoHandlerFoundException ex) {
        logger.error(ex.getMessage());
        return new ResponseEntity<>(
                Map.of(
                        WebConstants.STATUS, WebConstants.FAIL,
                        WebConstants.MESSAGE, WebConstants.UNKNOWN_ERROR
                ),
                HttpStatus.NOT_FOUND
        );

    }




}
