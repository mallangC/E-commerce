package com.zb.ecommerce.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JWTUtil {
  private final SecretKey secretKey;
  @Value("${jwt.access-token-time}")
  private Long accessTokenExpiration;
  @Value("${jwt.refresh-token-time}")
  private Long refreshTokenExpiration;

  public JWTUtil(@Value("${jwt.secretkey}")String secret) {
    secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),
            Jwts.SIG.HS256.key().build().getAlgorithm());

  }


  public String getUsername(String token) {
    try{
      return Jwts.parser().verifyWith(secretKey).build()
              .parseSignedClaims(token)
              .getPayload()
              .get("username",String.class);
    }catch (ExpiredJwtException e){
      return e.getClaims().get("username").toString();
    }catch (JwtException e){
      return null;
    }
  }


  public String getRole(String token) {
    try{
      return Jwts.parser().verifyWith(secretKey).build()
              .parseSignedClaims(token)
              .getPayload()
              .get("role",String.class);
    }catch (ExpiredJwtException e){
      return e.getClaims().get("role").toString();
    }catch (JwtException e){
      return null;
    }
  }


  public Boolean isExpired(String token) {
    try{
      return Jwts.parser().verifyWith(secretKey).build()
              .parseSignedClaims(token)
              .getPayload()
              .getExpiration()
              .before(new Date());
    }catch (ExpiredJwtException e){
      return true;
    }catch (JwtException e){
      return null;
    }
  }


  public String generateAccessToken(String username, String role) {
    return createJwt(username, role, accessTokenExpiration);
  }


  public String generateRefreshToken(String username, String role) {
    return createJwt(username, role, refreshTokenExpiration);
  }


  public String createJwt(String username, String role, Long expiredMs){
    return Jwts.builder()
            .claim("username",username)
            .claim("role",role)
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + expiredMs))
            .signWith(secretKey)
            .compact();
  }
}
