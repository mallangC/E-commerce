package com.zb.ecommerce.domain.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
  @Pattern(regexp = "^.{0}$|^[0-9]+$", message = "숫자만 입력해주세요")
  private String quantity;
}
