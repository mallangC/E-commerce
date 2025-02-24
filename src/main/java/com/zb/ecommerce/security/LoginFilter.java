package com.zb.ecommerce.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zb.ecommerce.domain.form.LoginForm;
import com.zb.ecommerce.service.RedisService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Iterator;

@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

  private final AuthenticationManager authenticationManager;
  private final JWTUtil jwtUtil;
  private final RedisService redisService;

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request,
                                              HttpServletResponse response) throws AuthenticationException
  {
    LoginForm loginForm = new LoginForm();

    try{
      ObjectMapper objectMapper = new ObjectMapper();
      ServletInputStream inputStream = request.getInputStream();
      String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
      loginForm = objectMapper.readValue(messageBody, LoginForm.class);
    }catch (IOException e){
      System.out.println("json parsing error"+e.getMessage());
    }

    String username = loginForm.getUsername();
    String password = loginForm.getPassword();

    UsernamePasswordAuthenticationToken authToken =
            new UsernamePasswordAuthenticationToken(username, password, null);

    return authenticationManager.authenticate(authToken);
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest request,
                                          HttpServletResponse response,
                                          FilterChain chain,
                                          Authentication authResult) throws IOException, ServletException {
    System.out.println("로그인 성공");
    CustomUserDetails userDetails = (CustomUserDetails) authResult.getPrincipal();
    String username = userDetails.getUsername();

    Collection<? extends GrantedAuthority> authorities = authResult.getAuthorities();
    Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
    GrantedAuthority auth = iterator.next();

    String role = auth.getAuthority();
    String accessToken = jwtUtil.generateAccessToken(username, role);
    String refreshToken = jwtUtil.generateRefreshToken(username, role);
    redisService.setRefreshToken(username, refreshToken);

    response.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
  }

  @Override
  protected void unsuccessfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            AuthenticationException failed) throws IOException, ServletException {
    System.out.println("로그인 실패");
    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
  }
}
