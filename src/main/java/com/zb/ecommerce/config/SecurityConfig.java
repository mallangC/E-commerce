package com.zb.ecommerce.config;

import com.zb.ecommerce.exception.CustomAccessDeniedHandler;
import com.zb.ecommerce.exception.CustomAuthenticationEntryPoint;
import com.zb.ecommerce.security.JWTFilter;
import com.zb.ecommerce.security.JWTUtil;
import com.zb.ecommerce.security.LoginFilter;
import com.zb.ecommerce.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final AuthenticationConfiguration authenticationConfiguration;
  private final CustomAccessDeniedHandler customAccessDeniedHandler;
  private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
  private final JWTUtil jwtUtil;
  private final RedisService redisService;

  @Bean
  public AuthenticationManager authenticationManager(
          AuthenticationConfiguration configuration) throws Exception {
    return configuration.getAuthenticationManager();
  }

  @Bean
  public BCryptPasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
            .csrf(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .logout(AbstractHttpConfigurer::disable);
    http
            .authorizeHttpRequests((auth) ->
                    auth.requestMatchers("/login", "/", "/join", "/email-auth", "/products", "/products/**", "/cart", "/carts").permitAll()
                            .requestMatchers("/product", "/product/*").hasRole("ADMIN")
                            .anyRequest().authenticated());
    http
            .addFilterBefore(new JWTFilter(jwtUtil, redisService), LoginFilter.class)
            .addFilterAt(new LoginFilter(
                    authenticationManager(authenticationConfiguration), jwtUtil, redisService),
                    UsernamePasswordAuthenticationFilter.class)
            .sessionManagement((session) ->
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
    http
            .exceptionHandling(e -> e
                    .accessDeniedHandler(customAccessDeniedHandler)
                    .authenticationEntryPoint(customAuthenticationEntryPoint));

    return http.build();
  }

  @Bean
  public WebSecurityCustomizer webSecurityCustomizer() {
    return (web) -> web.ignoring()
            .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
  }
}
