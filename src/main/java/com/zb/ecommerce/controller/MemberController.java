package com.zb.ecommerce.controller;

import com.zb.ecommerce.domain.form.JoinForm;
import com.zb.ecommerce.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class MemberController {

  private final MemberService userService;

  @GetMapping("/email-auth")
  public String index(@RequestParam String id) {
    userService.emailAuth(id);
    return "회원이 인증되었습니다.";
  }

  @PostMapping("/join")
  public ResponseEntity<?> addUser(@RequestBody JoinForm form) {
    userService.addMember(form);
    return ResponseEntity.ok(form.getEmail()+" 으로 회원가입 인증 메일이 전송되었습니다.");
  }

}
