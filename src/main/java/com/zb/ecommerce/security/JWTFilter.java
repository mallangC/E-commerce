package com.zb.ecommerce.security;

import com.zb.ecommerce.domain.type.MemberType;
import com.zb.ecommerce.model.Member;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

  private final JWTUtil jwtUtil;

  @Override
  protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {
    String authorization = request.getHeader("Authorization");

    if (authorization == null || authorization.startsWith("Bearer ")) {
      log.info("token null");
      filterChain.doFilter(request, response);
      return;
    }

    log.info("got token");

    String token = authorization.split(" ")[1];

    if (jwtUtil.isExpired(token)) {
      log.info("token expired");
      filterChain.doFilter(request, response);
      return;
    }

    String username = jwtUtil.getUsername(token);
    String role = jwtUtil.getRole(token);

    Member member = null;

    if (role.contains("USER")){
      member = Member.builder()
              .email(username)
              .password("temppassword")
              .role(MemberType.ROLE_USER)
              .build();
    }else {
      member = Member.builder()
              .email(username)
              .password("temppassword")
              .role(MemberType.ROLE_ADMIN)
              .build();
    }

    CustomUserDetails customUserDetails = new CustomUserDetails(member);

    Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails,
            null ,
            customUserDetails.getAuthorities());

    SecurityContextHolder.getContext().setAuthentication(authToken);

    filterChain.doFilter(request, response);
  }
}
