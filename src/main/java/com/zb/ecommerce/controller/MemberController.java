package com.zb.ecommerce.controller;

import com.zb.ecommerce.domain.dto.MemberDto;
import com.zb.ecommerce.domain.form.JoinForm;
import com.zb.ecommerce.domain.form.LoginForm;
import com.zb.ecommerce.domain.form.MemberUpdateForm;
import com.zb.ecommerce.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class MemberController {

  private final MemberService memberService;

  @GetMapping("/email-auth")
  public String index(@RequestParam String email,@RequestParam String code) {
    memberService.emailAuth(email, code);
    return "회원이 인증되었습니다.";
  }


  @PostMapping("/join")
  public ResponseEntity<?> addUser(@Valid @RequestBody JoinForm form) {
    memberService.addMember(form);
    return ResponseEntity.ok(form.getEmail()+" 으로 회원가입 인증 메일이 전송되었습니다.");
  }


  @GetMapping("/member")
  public ResponseEntity<MemberDto> getMember() {
    String tokenEmail = SecurityContextHolder.getContext().getAuthentication().getName();
    return ResponseEntity.ok(memberService.getMemberDetail(tokenEmail));
  }


  @PatchMapping("/member")
  public ResponseEntity<MemberDto> updateMember(@Valid @RequestBody MemberUpdateForm form) {
    String tokenEmail = SecurityContextHolder.getContext().getAuthentication().getName();
    return ResponseEntity.ok(memberService.updateMemberDetail(form, tokenEmail));
  }


  @PostMapping("/logout")
  public ResponseEntity<String> logout(HttpServletRequest request) {
    String token  = request.getHeader(HttpHeaders.AUTHORIZATION);

    if (memberService.logout(token)){
      return ResponseEntity.ok("로그아웃 완료");
    }
    return ResponseEntity.ok("유효한 토큰이 없습니다.");
  }


  @DeleteMapping("/member")
  public ResponseEntity<MemberDto> deleteMember(@Valid @RequestBody LoginForm form) {
    String tokenEmail = SecurityContextHolder.getContext().getAuthentication().getName();
    return ResponseEntity.ok(memberService.deleteMember(form, tokenEmail));
  }
}
