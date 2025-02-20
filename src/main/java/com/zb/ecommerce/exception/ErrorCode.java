package com.zb.ecommerce.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {
  NOT_FOUND_USER(HttpStatus.BAD_REQUEST, "회원을 찾을 수 없습니다."),
  NOT_VERIFIED_USER(HttpStatus.BAD_REQUEST, "인증되지 않은 아이디입니다."),
  ALREADY_REGISTERED_USER(HttpStatus.BAD_REQUEST, "이미 가입된 아이디입니다."),


  DENIED_TOKEN(HttpStatus.BAD_REQUEST, "맞는 권한을 가진 사용자가 아닙니다."),
  EXPIRED_TOKEN(HttpStatus.BAD_REQUEST, "토큰 기간이 만료되었습니다."),
  NOT_FOUND_TOKEN(HttpStatus.BAD_REQUEST, "인증되지 않은 사용자 입니다."),
  ;

  private final HttpStatus httpStatus;
  private final String message;
}
