package com.zb.ecommerce.model;

import com.zb.ecommerce.domain.form.MemberUpdateForm;
import com.zb.ecommerce.domain.type.MemberType;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
  @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE, orphanRemoval = true)
  private List<CartProduct> cart;

  public void emailVerify(){
    this.isEmailVerified = true;
  }

  public void memberChangePassword(String password){
    this.password = password;
  }

  public void memberUpdate(MemberUpdateForm form){
    if (form.getName() != null){
      this.name = form.getName();
    }
    if (form.getPhone() != null){
      this.phone = form.getPhone();
    }
    if (form.getAddress() != null){
      this.address = form.getAddress();
    }
    if (form.getAddressDetail() != null){
      this.addressDetail = form.getAddressDetail();
    }
  }

}
