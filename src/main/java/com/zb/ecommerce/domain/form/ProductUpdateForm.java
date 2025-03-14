package com.zb.ecommerce.domain.form;

import com.zb.ecommerce.domain.type.CategoryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductUpdateForm {

  private String name;
  @NotBlank(message = "코드를 입력해주세요.")
  private String code;
  private String changeCode;
  private CategoryType categoryType;
  private String description;
  @Pattern(regexp = "^.{0}$|^.{4,}$", message = "1000원 이상의 숫자만 입력해주세요")
  private String price;
  private String image;
}
