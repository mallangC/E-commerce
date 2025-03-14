package com.zb.ecommerce.domain.form;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CartAddForm {
  @NotBlank(message = "상품 코드를 입력해주세요")
  private String productCode;
  @NotBlank(message = "상품 사이즈를 입력해주세요")
  private String size;
  @Min(value = 1, message = "1이상의 숫자만 입력해주세요")
  private Integer quantity;
}
