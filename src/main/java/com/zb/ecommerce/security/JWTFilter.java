package com.zb.ecommerce.security;

import com.zb.ecommerce.service.RedisService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;


@Slf4j
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

  private final JWTUtil jwtUtil;
  private final RedisService redisService;

  @Override
  protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {


    String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

    if (authorization == null || !authorization.startsWith("Bearer ")) {
      log.info("token null");
      filterChain.doFilter(request, response);
      return;
    }

    log.info("got token");

    String token = authorization.split(" ")[1];
    if (redisService.getLogoutToken(token).isPresent()) {
      log.info("logout token");
      filterChain.doFilter(request, response);
      return;
    }

    String username = jwtUtil.getUsername(token);
    String role = jwtUtil.getRole(token);

    if (jwtUtil.isExpired(token)) {
      if (redisService.getRefreshToken(username).isEmpty()) {
        log.info("token expired");
        filterChain.doFilter(request, response);
        return;
      }else {
        String accessToken = jwtUtil.generateAccessToken(username, role);
        response.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
      }
    }

    Collection<GrantedAuthority> authority = new ArrayList<>();
    authority.add(new GrantedAuthority() {
      @Override
      public String getAuthority() {
        return role;
      }
    });

    Authentication authToken = new UsernamePasswordAuthenticationToken(
            username,
            null ,
            authority);

    SecurityContextHolder.getContext().setAuthentication(authToken);

    filterChain.doFilter(request, response);
  }
}
