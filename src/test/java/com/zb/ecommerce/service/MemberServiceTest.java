package com.zb.ecommerce.service;

import com.zb.ecommerce.component.MailComponent;
import com.zb.ecommerce.domain.dto.MemberDto;
import com.zb.ecommerce.domain.form.JoinForm;
import com.zb.ecommerce.domain.form.LoginForm;
import com.zb.ecommerce.domain.form.MemberUpdateForm;
import com.zb.ecommerce.exception.CustomException;
import com.zb.ecommerce.model.Member;
import com.zb.ecommerce.repository.MemberRepository;
import com.zb.ecommerce.security.JWTUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static com.zb.ecommerce.exception.ErrorCode.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {
  @Mock
  private MemberRepository memberRepository;

  @Mock
  private JWTUtil jwtUtil;

  @Mock
  private MailComponent mailComponent;

  @Mock
  private ValueOperations<String,Object> valueOperations;

  @Mock
  private RedisService redisService;

  @InjectMocks
  private MemberService memberService;

  @Mock
  private PasswordEncoder passwordEncoder;

  JoinForm form = JoinForm.builder()
          .email("email")
          .password("password")
          .phone("01012345678")
          .name("Tod")
          .address("address")
          .addressDetail("addressDetail")
          .build();

  @Test
  @DisplayName("회원 추가 성공")
  void addMember() {
    //given
    given(memberRepository.existsByEmail(anyString()))
            .willReturn(false);
    ArgumentCaptor<Member> captor = ArgumentCaptor.forClass(Member.class);
    //when
    memberService.addMember(form);
    //then
    verify(memberRepository,times(1)).save(captor.capture());
    assertEquals(form.getEmail(), captor.getValue().getEmail());
    assertEquals(form.getName(), captor.getValue().getName());
    assertEquals(form.getPhone(), captor.getValue().getPhone());
    assertEquals(form.getAddress(), captor.getValue().getAddress());
    assertEquals(form.getAddressDetail(), captor.getValue().getAddressDetail());
  }

  @Test
  @DisplayName("회원 추가 실패(이미 가입한 이메일)")
  void addMemberFailure1() {
    //given
    given(memberRepository.existsByEmail(anyString()))
            .willReturn(true);
    try{
      //when
      memberService.addMember(form);
    }catch (CustomException e){
      //then
      assertEquals(ALREADY_REGISTERED_MEMBER, e.getErrorCode());
    }
  }


  //--------


  @Test
  @DisplayName("이메일 인증 성공")
  void emailAuth() {
    //given
    given(memberRepository.findByEmail(anyString()))
            .willReturn(Optional.of(Member.builder()
                    .id(1L)
                    .email("email")
                    .password("password")
                    .phone("01012345678")
                    .name("Tod")
                    .address("address")
                    .addressDetail("addressDetail")
                    .isEmailVerified(false)
                    .build()));

    given(redisService.getCode(anyString())).willReturn("code");
    //when
    memberService.emailAuth("email", "code");
    //then
    verify(redisService,times(1)).getCode(anyString());
  }


  @Test
  @DisplayName("이메일 인증 실패(회원을 찾을 수 없음)")
  void emailAuthFailure1() {
    //given
    given(memberRepository.findByEmail(anyString()))
            .willReturn(Optional.empty());
    try{
      //when
      memberService.emailAuth("email", "code");
    }catch (CustomException e){
      //then
      assertEquals(NOT_FOUND_MEMBER, e.getErrorCode());
    }
  }


  @Test
  @DisplayName("이메일 인증 실패(이미 인증된 이메일)")
  void emailAuthFailure2() {
    //given
    given(memberRepository.findByEmail(anyString()))
            .willReturn(Optional.of(Member.builder()
                    .id(1L)
                    .email("email")
                    .password("password")
                    .phone("01012345678")
                    .name("Tod")
                    .address("address")
                    .addressDetail("addressDetail")
                    .isEmailVerified(true)
                    .build()));
    try{
      //when
      memberService.emailAuth("email", "code");
    }catch (CustomException e){
      //then
      assertEquals(ALREADY_VERIFIED_EMAIL, e.getErrorCode());
    }
  }


  @Test
  @DisplayName("이메일 인증 실패(인증 번호가 맞지않음)")
  void emailAuthFailure3() {
    //given
    given(memberRepository.findByEmail(anyString()))
            .willReturn(Optional.of(Member.builder()
                    .id(1L)
                    .email("email")
                    .password("password")
                    .phone("01012345678")
                    .name("Tod")
                    .address("address")
                    .addressDetail("addressDetail")
                    .isEmailVerified(false)
                    .build()));

    given(redisService.getCode(anyString())).willReturn("notGoodCode");

    try{
      //when
      memberService.emailAuth("email", "code");
    }catch (CustomException e){
      //then
      assertEquals(NOT_MATCH_VERIFICATION_CODE, e.getErrorCode());
    }
  }


  //--------


  @Test
  @DisplayName("로그아웃 성공")
  void logout() {
    //given
    String token = "Bearer asdfql6kd6j9aslkd3lkasEdsasdasdaqq12";
    //when
     boolean isLogout = memberService.logout(token);
    //then
    assertTrue(isLogout);
  }


  @Test
  @DisplayName("로그아웃 실패(토큰이 없음)")
  void logoutFailure1() {
    //given
    String token = null;
    //when
    boolean isLogout = memberService.logout(token);
    //then
    assertFalse(isLogout);
  }


  @Test
  @DisplayName("로그아웃 실패(토큰이 Bearer로 시작하지 않음)")
  void logoutFailure2() {
    //given
    String token = "asdfql6kd6j9aslkd3lkasEdsasdasdaqq12";
    //when
    boolean isLogout = memberService.logout(token);
    //then
    assertFalse(isLogout);
  }


  //--------


  @Test
  @DisplayName("회원정보 수정 성공(모든 정보)")
  void updateMemberByEmail1() {
    //given
    given(memberRepository.findByEmail(anyString()))
            .willReturn(Optional.of(Member.builder()
                            .id(1L)
                            .email("email")
                            .password("password")
                            .phone("01012345678")
                            .name("Tod")
                            .address("address")
                            .addressDetail("addressDetail")
                            .isEmailVerified(true)
                    .build()));

    given(passwordEncoder.matches(anyString(), anyString()))
            .willReturn(true);

    MemberUpdateForm updateForm = MemberUpdateForm.builder()
            .curPassword("password")
            .changePassword("change_password")
            .name("Linda")
            .phone("01098765432")
            .address("change_address")
            .addressDetail("change_addressDetail")
            .build();

    //when
    MemberDto memberDto = memberService.updateMemberDetail(updateForm, "email");
    //then
    assertEquals("Linda", memberDto.getName());
    assertEquals("01098765432", memberDto.getPhone());
    assertEquals("change_address", memberDto.getAddress());
    assertEquals("change_addressDetail", memberDto.getAddressDetail());
  }


  @Test
  @DisplayName("회원정보 수정 성공(이름)")
  void updateMemberByEmail2() {
    //given
    given(memberRepository.findByEmail(anyString()))
            .willReturn(Optional.of(Member.builder()
                    .id(1L)
                    .email("email")
                    .password("password")
                    .phone("01012345678")
                    .name("Tod")
                    .address("address")
                    .addressDetail("addressDetail")
                    .isEmailVerified(true)
                    .build()));

    given(passwordEncoder.matches(anyString(), anyString()))
            .willReturn(true);

    MemberUpdateForm updateForm = MemberUpdateForm.builder()
            .curPassword("password")
            .name("Linda")
            .build();

    //when
    MemberDto memberDto = memberService.updateMemberDetail(updateForm, "email");
    //then
    assertEquals("Linda", memberDto.getName());
    assertEquals("01012345678", memberDto.getPhone());
    assertEquals("address", memberDto.getAddress());
    assertEquals("addressDetail", memberDto.getAddressDetail());
  }


  @Test
  @DisplayName("회원정보 수정 성공(핸드폰 번호)")
  void updateMemberByEmail3() {
    //given
    given(memberRepository.findByEmail(anyString()))
            .willReturn(Optional.of(Member.builder()
                    .id(1L)
                    .email("email")
                    .password("password")
                    .phone("01012345678")
                    .name("Tod")
                    .address("address")
                    .addressDetail("addressDetail")
                    .isEmailVerified(true)
                    .build()));

    given(passwordEncoder.matches(anyString(), anyString()))
            .willReturn(true);

    MemberUpdateForm updateForm = MemberUpdateForm.builder()
            .curPassword("password")
            .phone("01098765432")
            .build();

    //when
    MemberDto memberDto = memberService.updateMemberDetail(updateForm, "email");
    //then
    assertEquals("Tod", memberDto.getName());
    assertEquals("01098765432", memberDto.getPhone());
    assertEquals("address", memberDto.getAddress());
    assertEquals("addressDetail", memberDto.getAddressDetail());
  }


  @Test
  @DisplayName("회원정보 수정 성공(주소)")
  void updateMemberByEmail4() {
    //given
    given(memberRepository.findByEmail(anyString()))
            .willReturn(Optional.of(Member.builder()
                    .id(1L)
                    .email("email")
                    .password("password")
                    .phone("01012345678")
                    .name("Tod")
                    .address("address")
                    .addressDetail("addressDetail")
                    .isEmailVerified(true)
                    .build()));

    given(passwordEncoder.matches(anyString(), anyString()))
            .willReturn(true);

    MemberUpdateForm updateForm = MemberUpdateForm.builder()
            .curPassword("password")
            .address("change_address")
            .addressDetail("change_addressDetail")
            .build();

    //when
    MemberDto memberDto = memberService.updateMemberDetail(updateForm, "email");
    //then
    assertEquals("Tod", memberDto.getName());
    assertEquals("01012345678", memberDto.getPhone());
    assertEquals("change_address", memberDto.getAddress());
    assertEquals("change_addressDetail", memberDto.getAddressDetail());
  }


  @Test
  @DisplayName("회원정보 수정 실패(비밀번호 틀림)")
  void updateMemberByEmailFailure1() {
    //given
    given(memberRepository.findByEmail(anyString()))
            .willReturn(Optional.of(Member.builder()
                    .id(1L)
                    .email("email")
                    .password("password")
                    .phone("01012345678")
                    .name("Tod")
                    .address("address")
                    .addressDetail("addressDetail")
                    .isEmailVerified(true)
                    .build()));

    given(passwordEncoder.matches(anyString(), anyString()))
            .willReturn(false);

    MemberUpdateForm updateForm = MemberUpdateForm.builder()
            .curPassword("password")
            .changePassword("change_password")
            .name("Linda")
            .phone("01098765432")
            .address("change_address")
            .addressDetail("change_addressDetail")
            .build();

    try{
      //when
      MemberDto memberDto = memberService.updateMemberDetail(updateForm, "email");
    }catch (CustomException e){
      //then
      assertEquals(NOT_MATCH_PASSWORD, e.getErrorCode());
    }
  }


  @Test
  @DisplayName("회원정보 수정 실패(바꾸려는 비밀번호가 원래 비밀번호와 동일)")
  void updateMemberByEmailFailure2() {
    //given
    given(memberRepository.findByEmail(anyString()))
            .willReturn(Optional.of(Member.builder()
                    .id(1L)
                    .email("email")
                    .password("password")
                    .phone("01012345678")
                    .name("Tod")
                    .address("address")
                    .addressDetail("addressDetail")
                    .isEmailVerified(true)
                    .build()));

    given(passwordEncoder.matches(anyString(), anyString()))
            .willReturn(true);

    MemberUpdateForm updateForm = MemberUpdateForm.builder()
            .curPassword("password")
            .changePassword("password")
            .name("Linda")
            .phone("01098765432")
            .address("change_address")
            .addressDetail("change_addressDetail")
            .build();

    try{
      //when
      MemberDto memberDto = memberService.updateMemberDetail(updateForm, "email");
    }catch (CustomException e){
      //then
      assertEquals(EQUAL_PASSWORD, e.getErrorCode());
    }
  }


  //-------


  @Test
  @DisplayName("회원탈퇴 성공")
  void deleteMember() {
    //given
    given(memberRepository.findByEmail(anyString()))
            .willReturn(Optional.of(Member.builder()
                    .id(1L)
                    .email("email")
                    .password("password")
                    .phone("01012345678")
                    .name("Tod")
                    .address("address")
                    .addressDetail("addressDetail")
                    .isEmailVerified(true)
                    .build()));

    given(passwordEncoder.matches(anyString(), anyString()))
            .willReturn(true);

    LoginForm loginForm = LoginForm.builder()
            .username("email")
            .password("password")
            .build();

    //when
    MemberDto memberDto = memberService.deleteMember(loginForm, "email");
    //then
    verify(memberRepository,times(1)).deleteByEmail(anyString());
  }


  @Test
  @DisplayName("회원탈퇴 실패(비밀번호가 틀림)")
  void deleteMemberFailure() {
    //given
    given(memberRepository.findByEmail(anyString()))
            .willReturn(Optional.of(Member.builder()
                    .id(1L)
                    .email("email")
                    .password("password")
                    .phone("01012345678")
                    .name("Tod")
                    .address("address")
                    .addressDetail("addressDetail")
                    .isEmailVerified(true)
                    .build()));

    given(passwordEncoder.matches(anyString(), anyString()))
            .willReturn(false);

    LoginForm loginForm = LoginForm.builder()
            .username("email")
            .password("password")
            .build();

    try {
      //when
      memberService.deleteMember(loginForm, "email");
    }catch (CustomException e){
      //then
      assertEquals(NOT_MATCH_PASSWORD, e.getErrorCode());
    }
  }


}