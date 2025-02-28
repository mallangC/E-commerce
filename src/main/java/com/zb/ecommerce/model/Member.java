package com.zb.ecommerce.model;

import com.zb.ecommerce.domain.type.MemberType;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@ToString
public class Member extends BaseEntity{
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(unique = true)
  private String email;
  private String password;
  private String name;
  private String phone;
  private String address;
  private String addressDetail;
  @Enumerated(EnumType.STRING)
  private MemberType role;
  private Boolean isEmailVerified;
  @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
  private List<CartProduct> cart;
}
