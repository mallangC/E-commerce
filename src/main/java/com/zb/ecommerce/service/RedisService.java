package com.zb.ecommerce.service;

import com.zb.ecommerce.exception.CustomException;
import com.zb.ecommerce.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {
  private final RedisTemplate<String, Object> redisTemplate;

  // 레디스에 이메일 & 인증 코드 저장
  public void setCode(String email, String code) {
    ValueOperations<String, Object> valueOperations =
            redisTemplate.opsForValue();
    valueOperations.set(email, code, 180, TimeUnit.SECONDS);
  }

  // 이메일로 레디스에 저장된 인증 코드 가져오기
  public String getCode(String email) {
    ValueOperations<String, Object> valueOperations =
            redisTemplate.opsForValue();
    Object code = valueOperations.get(email);
    if (code == null) {
      throw new CustomException(ErrorCode.EXPIRED_VERIFY_EMAIL);
    }
    return code.toString();
  }

  public void setLogoutToken(String accessToken) {
    String withoutBearerToken = accessToken.split(" ")[1];
    redisTemplate.opsForValue().set(withoutBearerToken, "logout", 60 * 30, TimeUnit.SECONDS);
  }

  public Optional<Object> getLogoutToken(String accessToken) {
    return Optional.ofNullable(redisTemplate.opsForValue().get(accessToken));
  }

  public void setRefreshToken(String email, String refreshToken) {
    redisTemplate.opsForValue().set(email, refreshToken, 60 * 60 * 24 * 7, TimeUnit.SECONDS);
  }

  public Optional<Object> getRefreshToken(String email) {
    return Optional.ofNullable(redisTemplate.opsForValue().get(email));
  }

  public void deleteRefreshToken(String email) {
    redisTemplate.delete(email);
  }


}
