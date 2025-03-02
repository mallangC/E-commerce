package com.zb.ecommerce.domain.dto;

import com.zb.ecommerce.model.CartProduct;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CartProductDto {
  private Long id;
  private String productName;
  private String size;
  private Integer quantity;

  public static CartProductDto from(CartProduct cartProduct) {
    return CartProductDto.builder()
            .id(cartProduct.getId())
            .productName(cartProduct.getProduct().getName())
            .size(cartProduct.getSize())
            .quantity(cartProduct.getQuantity())
            .build();
  }
}
