package com.zb.ecommerce.model;

import com.zb.ecommerce.domain.type.MemberType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
public class Member extends BaseEntity{
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String email;
  private String password;
  private String name;
  private String phone;
  private String address;
  private String addressDetail;
  @Enumerated(EnumType.STRING)
  private MemberType role;
  private Boolean isEmailVerified;
  private String verificationCode;
}
