package com.zb.ecommerce.domain.dto;

import com.zb.ecommerce.model.ProductDetail;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductDetailDto {
  private String size;
  private Integer quantity;

  public static ProductDetailDto from(ProductDetail detail) {
    return ProductDetailDto.builder()
            .size(detail.getSize())
            .quantity(detail.getQuantity())
            .build();

  }
}
