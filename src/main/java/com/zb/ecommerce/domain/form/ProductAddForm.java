package com.zb.ecommerce.domain.form;

import com.zb.ecommerce.domain.type.CategoryType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductAddForm {
  @NotBlank
  private String name;
  @NotBlank
  private String code;
  @NotNull
  private CategoryType category;
  private String description;
  private String image;
  @Min(value = 1000, message = "1000원 이상의 숫자만 입력해주세요")
  private Long price;
}
