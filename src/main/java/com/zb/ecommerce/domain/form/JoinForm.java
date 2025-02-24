package com.zb.ecommerce.domain.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

@Getter
@Builder
public class JoinForm {
  // 문자열이 0~9, a~z, A~Z로 시작하고 중간에 -_.이 들어갈수도 안들어갈수도있다-(^,?)
  // @ 앞에는 반드시 숫자나 문자가 존재하고-(*)
  // @ 뒤에는 글자가 반드시 존재하고 0~9, a~z, A~Z로 시작하고 중간에 -_.이 들어갈수도 안들어갈수도있다-(+, ?)
  // .뒤에는 a~z, A~Z의 글자가 2~3개 들어가고 문자열이 끝난다-($)
  @Pattern(regexp="^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])+[.][a-zA-Z]{2,3}$", message = "이메일을 확인해주세요.")
  private String email;
  @NotBlank(message = "비밀번호를 입력해주세요.")
  private String password;
  @NotBlank(message = "이름을 입력해주세요.")
  private String name;
  @Length(min = 10, max = 11, message = "핸드폰 번호를 확인해주세요.")
  private String phone;
  private String address;
  private String addressDetail;
}
