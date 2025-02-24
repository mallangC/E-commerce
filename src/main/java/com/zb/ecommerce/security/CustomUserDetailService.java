package com.zb.ecommerce.security;

import com.zb.ecommerce.exception.CustomException;
import com.zb.ecommerce.exception.ErrorCode;
import com.zb.ecommerce.model.Member;
import com.zb.ecommerce.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

  private final MemberRepository memberRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

    Member member = memberRepository.findByEmail(username)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MEMBER));

    if (!member.getIsEmailVerified()){
      throw new CustomException(ErrorCode.NOT_VERIFIED_MEMBER);
    }

    return new CustomUserDetails(member);
  }


}
