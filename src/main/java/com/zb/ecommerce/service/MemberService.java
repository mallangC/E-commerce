package com.zb.ecommerce.service;

import com.zb.ecommerce.component.MailComponent;
import com.zb.ecommerce.domain.dto.MemberDto;
import com.zb.ecommerce.domain.form.JoinForm;
import com.zb.ecommerce.domain.form.LoginForm;
import com.zb.ecommerce.domain.form.MemberUpdateForm;
import com.zb.ecommerce.domain.type.MemberType;
import com.zb.ecommerce.exception.CustomException;
import com.zb.ecommerce.model.Member;
import com.zb.ecommerce.repository.MemberRepository;
import com.zb.ecommerce.security.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.zb.ecommerce.exception.ErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;
  private final MailComponent mailComponent;
  private final RedisService redisService;
  private final JWTUtil jwtUtil;


  public void addMember(JoinForm form) {
    boolean isExist = memberRepository.existsByEmail(form.getEmail());

    if (isExist) {
      throw new CustomException(ALREADY_REGISTERED_MEMBER);
    }

    String code = UUID.randomUUID().toString();
    String email = form.getEmail();
    sendMail(email, code);

    redisService.setCode(email, code);
    memberRepository.save(from(form));
  }


  private void sendMail(String email, String code) {
    String title = "가입을 축하드립니다.";
    String text = "<p>가입을 축하드립니다.</p>" +
            "<p>아래의 링크를 클릭하셔서 가입을 완료하세요.</p>" +
            "<div><a href='http://localhost:8080/email-auth?" +
            "email=" + email +
            "&code=" + code +
            "'>가입 완료</a></div>";

    mailComponent.sendMail(email, title, text);
  }


  @Transactional
  public void emailAuth(String email, String code){

    Member member = findMemberByEmail(email);

    if (member.getIsEmailVerified()){
      throw new CustomException(ALREADY_VERIFIED_EMAIL);
    }

    String redisCode = redisService.getCode(email);
    if (!redisCode.equals(code)) {
      throw new CustomException(NOT_MATCH_VERIFICATION_CODE);
    }
    member.emailVerify();
  }


  public boolean logout(String token) {
    if (token != null && token.startsWith("Bearer ")) {
      String email = jwtUtil.getUsername(token.split(" ")[1]);
      redisService.deleteRefreshToken(email);
      redisService.setLogoutToken(token);
      return true;
    }
    return false;
  }


  public MemberDto getMemberDetail(String email){
    return MemberDto.from(findMemberByEmail(email));
  }


  @Transactional
  public MemberDto updateMemberDetail(MemberUpdateForm form, String tokenEmail){
    Member member = findMemberByEmail(tokenEmail);
    if (!passwordEncoder.matches(form.getCurPassword(), member.getPassword())) {
      throw new CustomException(NOT_MATCH_PASSWORD);
    }
    if (form.getChangePassword() != null){
      if (form.getCurPassword().equals(form.getChangePassword())) {
        throw new CustomException(EQUAL_PASSWORD);
      }
      member.memberChangePassword(passwordEncoder.encode(form.getChangePassword()));
    }
    member.memberUpdate(form);
    return MemberDto.from(member);
  }


  @Transactional
  public MemberDto deleteMember(LoginForm form, String tokenEmail){
    String password = form.getPassword();
    Member member = findMemberByEmail(tokenEmail);

    if (!passwordEncoder.matches(password, member.getPassword())) {
      throw new CustomException(NOT_MATCH_PASSWORD);
    }
    memberRepository.deleteByEmail(tokenEmail);
    redisService.deleteRefreshToken(tokenEmail);
    return MemberDto.from(member);
  }

  private Member findMemberByEmail(String email){
    return memberRepository.findByEmail(email)
            .orElseThrow(()-> new CustomException(NOT_FOUND_MEMBER));
  }

  private Member from(JoinForm form) {
    return Member.builder()
            .email(form.getEmail())
            .password(passwordEncoder.encode(form.getPassword()))
            .name(form.getName())
            .phone(form.getPhone())
            .address(form.getAddress())
            .addressDetail(form.getAddressDetail())
            .role(MemberType.ROLE_MEMBER)
            .isEmailVerified(false)
            .build();
  }

}
