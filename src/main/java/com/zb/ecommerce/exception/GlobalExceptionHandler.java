package com.zb.ecommerce.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


  @ExceptionHandler(CustomException.class)
  public ResponseEntity<String> customExceptionHandler(CustomException e) {
    log.error("CustomException is occurred : {}", e.getMessage());
    return ResponseEntity
            .status(e.getErrorCode().getHttpStatus())
            .body(new CustomException(e.getErrorCode()).getMessage());
  }


  @ExceptionHandler(Exception.class)
  public ResponseEntity<String> exceptionHandler(Exception e) {
    log.error("Exception is occurred : {}", e.getMessage());
    return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(e.getMessage());
  }
}
