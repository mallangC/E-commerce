package com.zb.ecommerce.service;

import com.zb.ecommerce.component.MailComponent;
import com.zb.ecommerce.domain.form.JoinForm;
import com.zb.ecommerce.exception.CustomException;
import com.zb.ecommerce.exception.ErrorCode;
import com.zb.ecommerce.model.Member;
import com.zb.ecommerce.domain.type.MemberType;
import com.zb.ecommerce.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;
  private final MailComponent mailComponent;

  public void addMember(JoinForm form) {
    boolean isExist = memberRepository.existsByEmail(form.getEmail());

    if (isExist) {
      throw new CustomException(ErrorCode.ALREADY_REGISTERED_USER);
    }

    String uuid = UUID.randomUUID().toString();
    memberRepository.save(from(form, uuid));

    String email = "kk3500@naver.com";
    String title = "가입을 축하드립니다.";
    String text = "<p>가입을 축하드립니다.</p>" +
            "<p>아래의 링크를 클릭하셔서 가입을 완료하세요.</p>" +
            "<div><a href='http://localhost:8080/email-auth?id=" +
            uuid +
            "'>가입 완료</a></div>";
    mailComponent.sendMail(email, title, text);
  }

  @Transactional
  public void emailAuth(String uuid){
    Member member = memberRepository.findByVerificationCode(uuid)
            .orElseThrow(()-> new CustomException(ErrorCode.NOT_FOUND_USER));

    member.setIsEmailVerified(true);
  }

  private Member from(JoinForm form, String uuid) {
    return Member.builder()
            .email(form.getEmail())
            .password(passwordEncoder.encode(form.getPassword()))
            .name(form.getName())
            .phone(form.getPhone())
            .address(form.getAddress())
            .addressDetail(form.getAddressDetail())
            .role(MemberType.ROLE_USER)
            .isEmailVerified(false)
            .verificationCode(uuid)
            .build();
  }

}
