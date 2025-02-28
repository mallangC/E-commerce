package com.zb.ecommerce.domain.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductAddForm {
  @NotBlank
  private String name;
  @NotBlank
  private String code;
  @NotBlank
  @Pattern(regexp = "^[a-zA-Z]+$", message = "숫자는 입력할 수 없습니다.")
  private String category;
  private String description;
  @NotBlank
  @Pattern(regexp = "^[0-9]{4,}$", message = "1000원 이상의 숫자만 입력해주세요")
  private String price;
}
