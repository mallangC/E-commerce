package com.zb.ecommerce.domain.form;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductDetailUpdateForm {
  @NotBlank(message = "코드를 입력해주세요")
  private String code;
  @NotBlank(message = "수정할 사이즈를 입력해주세요")
  private String size;
  private String changeSize;
  @Min(value = 1, message = "1이상의 숫자만 입력해주세요")
  private Integer quantity;
}
