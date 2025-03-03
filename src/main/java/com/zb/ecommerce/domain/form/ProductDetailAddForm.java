package com.zb.ecommerce.domain.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductDetailAddForm {
  @NotBlank
  private String code;
  @NotBlank
  @Pattern(regexp = "^[0-9a-zA-Z]+$", message = "사이즈를 입력해주세요")
  private String size;
  @NotBlank
  @Pattern(regexp = "^[1-9][0-9]*$", message = "1개 이상의 숫자만 입력해주세요")
  private String quantity;
}
