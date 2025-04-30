package com.zb.ecommerce.domain.dto;

import com.zb.ecommerce.model.CartProduct;
import lombok.*;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CartProductDto {
  private Long id;
  private String productName;
  private String size;
  private Integer quantity;
  private Long price;
  private String image;

  public static CartProductDto from(CartProduct cartProduct) {
    return CartProductDto.builder()
            .id(cartProduct.getId())
            .productName(cartProduct.getProduct().getName())
            .size(cartProduct.getSize())
            .quantity(cartProduct.getQuantity())
            .price(cartProduct.getProduct().getPrice())
            .image(cartProduct.getProduct().getImageUrl())
            .build();
  }
}
