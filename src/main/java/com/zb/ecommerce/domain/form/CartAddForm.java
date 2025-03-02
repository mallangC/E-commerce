package com.zb.ecommerce.domain.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CartAddForm {
  @NotBlank(message = "상품 코드를 입력해주세요")
  private String productCode;
  @NotBlank(message = "상품 사이즈를 입력해주세요")
  private String size;  
  @Pattern(regexp = "^.{0}$|^[1-9][0-9]*$", message = "숫자만 입력해주세요")
  private String quantity;
}
