package com.zb.ecommerce.domain.form;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberUpdateForm {
  @NotBlank
  private String curPassword;
  private String changePassword;
  private String name;
  private String phone;
  private String address;
  private String addressDetail;
}
