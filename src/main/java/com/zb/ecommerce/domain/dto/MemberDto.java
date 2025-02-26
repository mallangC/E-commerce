package com.zb.ecommerce.domain.dto;

import com.zb.ecommerce.domain.type.MemberType;
import com.zb.ecommerce.model.Member;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class MemberDto {
  private Long id;
  private String email;
  private String name;
  private String phone;
  private String address;
  private String addressDetail;
  private MemberType role;
  private LocalDateTime createAt;
  private LocalDateTime updateAt;

  public static MemberDto from(Member member) {
    return MemberDto.builder()
            .id(member.getId())
            .email(member.getEmail())
            .name(member.getName())
            .phone(member.getPhone())
            .address(member.getAddress())
            .addressDetail(member.getAddressDetail())
            .role(member.getRole())
            .createAt(member.getCreateAt())
            .updateAt(member.getUpdateAt())
            .build();
  }
}
