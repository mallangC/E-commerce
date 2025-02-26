package com.zb.ecommerce.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, String>> validException(MethodArgumentNotValidException e) {
    Map<String, String> errors = new HashMap<>();
    e.getBindingResult().getFieldErrors().forEach(error ->
            errors.put(error.getField(), error.getDefaultMessage()));
    log.info("validException is occurred : {}", e.getMessage());

    return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(errors);
  }

  @ExceptionHandler(CustomException.class)
  public ResponseEntity<String> customExceptionHandler(CustomException e) {
    log.info("CustomException is occurred : {}", e.getMessage());

    return ResponseEntity
            .status(e.getErrorCode().getHttpStatus())
            .body(e.getMessage());
  }


  @ExceptionHandler(Exception.class)
  public ResponseEntity<String> exceptionHandler(Exception e) {
    log.info("Exception is occurred : {}", e.getMessage());

    return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(e.getMessage());
  }
}
