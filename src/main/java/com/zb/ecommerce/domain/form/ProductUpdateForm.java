package com.zb.ecommerce.domain.form;

import com.zb.ecommerce.domain.type.CategoryType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import software.amazon.awssdk.annotations.NotNull;

@Getter
@Builder
public class ProductUpdateForm {

  private String name;
  @NotBlank(message = "코드를 입력해주세요.")
  private String code;
  private String changeCode;
  private CategoryType categoryType;
  private String description;
  @NotNull
  @Min(value = 1000, message = "1000원 이상의 가격을 입력해주세요.")
  private Long price;
  private String image;
}
