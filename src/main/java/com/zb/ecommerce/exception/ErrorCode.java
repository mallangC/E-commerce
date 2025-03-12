package com.zb.ecommerce.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {
  NOT_FOUND_MEMBER(HttpStatus.BAD_REQUEST, "회원을 찾을 수 없습니다."),
  NOT_VERIFIED_MEMBER(HttpStatus.BAD_REQUEST, "인증되지 않은 아이디입니다."),
  ALREADY_REGISTERED_MEMBER(HttpStatus.BAD_REQUEST, "이미 가입된 아이디입니다."),
  EXPIRED_VERIFY_EMAIL(HttpStatus.BAD_REQUEST, "이메일 인증 기간이 만료되었습니다."),
  NOT_MATCH_VERIFICATION_CODE(HttpStatus.BAD_REQUEST, "인증 번호가 맞지 않습니다."),
  ALREADY_VERIFIED_EMAIL(HttpStatus.BAD_REQUEST, "이미 이메일 인증이 되었습니다."),
  NOT_MATCH_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호가 맞지 않습니다."),
  EQUAL_PASSWORD(HttpStatus.BAD_REQUEST, "똑같은 비밀번호로 변경할 수 없습니다."),


  DENIED_TOKEN(HttpStatus.BAD_REQUEST, "맞는 권한을 가진 사용자가 아닙니다."),
  EXPIRED_TOKEN(HttpStatus.BAD_REQUEST, "토큰 기간이 만료되었습니다."),
  NOT_FOUND_TOKEN(HttpStatus.BAD_REQUEST, "인증되지 않은 사용자 입니다."),


  SEND_EMAIL_FAIL(HttpStatus.BAD_REQUEST, "이메일 전송에 실패했습니댜."),
  NOT_ALLOW_BLANK(HttpStatus.BAD_REQUEST, "이메일값이 비어있습니다."),


  ALREADY_ADDED_PRODUCT(HttpStatus.BAD_REQUEST, "이미 추가된 상품입니다."),
  NOT_FOUND_PRODUCT(HttpStatus.BAD_REQUEST, "상품을 찾을 수 없습니다."),
  ALREADY_ADDED_SIZE(HttpStatus.BAD_REQUEST, "이미 추가된 사이즈입니다."),
  NOT_FOUND_SIZE(HttpStatus.BAD_REQUEST, "해당 사이즈를 찾을 수 없습니다."),
  NOT_ENOUGH_PRODUCT(HttpStatus.BAD_REQUEST, "상품 갯수가 충분하지 않습니다."),


  NOT_FOUND_CART_PRODUCT(HttpStatus.BAD_REQUEST, "카트에 담긴 상품을 찾을 수 없습니다."),
  
  
  WRONG_FILE(HttpStatus.BAD_REQUEST, "파일을 확인해주세요"),

  ;

  private final HttpStatus httpStatus;
  private final String message;
}
