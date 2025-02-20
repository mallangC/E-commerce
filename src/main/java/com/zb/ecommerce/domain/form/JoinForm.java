package com.zb.ecommerce.domain.form;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JoinForm {
  private String email;
  private String password;
  private String name;
  private String phone;
  private String address;
  private String addressDetail;
}
