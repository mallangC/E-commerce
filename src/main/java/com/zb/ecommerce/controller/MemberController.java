package com.zb.ecommerce.controller;

import com.zb.ecommerce.domain.dto.MemberDto;
import com.zb.ecommerce.domain.form.JoinForm;
import com.zb.ecommerce.domain.form.LoginForm;
import com.zb.ecommerce.domain.form.MemberUpdateForm;
import com.zb.ecommerce.response.HttpApiResponse;
import com.zb.ecommerce.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberController {

  private final MemberService memberService;


  @PostMapping("/join")
  public ResponseEntity<HttpApiResponse<String>> addMember(@Valid @RequestBody JoinForm form) {
    memberService.addMember(form);
    return ResponseEntity.ok(new HttpApiResponse<>(
            form.getEmail() + " 으로 회원가입 인증 메일이 전송되었습니다.",
            "회원가입 인증 메일 전송 성공",
            HttpStatus.OK
    ));
  }


  @GetMapping("/email-auth")
  public ResponseEntity<HttpApiResponse<String>> emailAuth(
          @RequestParam String email,
          @RequestParam String code) {
    memberService.emailAuth(email, code);
    return ResponseEntity.ok(new HttpApiResponse<>(
            "회원이 인증되었습니다.",
            "회원 인증 성공",
            HttpStatus.OK
    ));
  }


  @GetMapping("/member")
  public ResponseEntity<HttpApiResponse<MemberDto>> getMemberProfile(
          @AuthenticationPrincipal String email) {
    return ResponseEntity.ok(new HttpApiResponse<>(
            memberService.getMemberDetail(email),
            "회원정보 조회 성공",
            HttpStatus.OK
    ));
  }


  @PatchMapping("/member")
  public ResponseEntity<HttpApiResponse<MemberDto>> updateMember(
          @Valid @RequestBody MemberUpdateForm form,
          @AuthenticationPrincipal String email) {
    return ResponseEntity.ok(new HttpApiResponse<>(
            memberService.updateMemberDetail(form, email),
            "회원정보 수정 성공",
            HttpStatus.OK
    ));
  }


  @PostMapping("/logout")
  public ResponseEntity<HttpApiResponse<String>> logout(HttpServletRequest request) {
    String token = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (memberService.logout(token)) {
      return ResponseEntity.ok(new HttpApiResponse<>(
              "로그아웃 완료",
              "로그아웃 성공",
              HttpStatus.OK
      ));
    }
    return ResponseEntity.ok(new HttpApiResponse<>(
            "유효한 토큰이 없습니다",
            "로그아웃 실패",
            HttpStatus.OK
    ));
  }


  @DeleteMapping("/member")
  public ResponseEntity<HttpApiResponse<MemberDto>> deleteMember(
          @Valid @RequestBody LoginForm form) {
    String tokenEmail = SecurityContextHolder.getContext().getAuthentication().getName();
    return ResponseEntity.ok(new HttpApiResponse<>(
            memberService.deleteMember(form, tokenEmail),
            "회원 탈퇴 성공",
            HttpStatus.OK
    ));
  }
}
