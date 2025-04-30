package com.zb.ecommerce.domain.dto;

import com.zb.ecommerce.domain.type.CategoryType;
import com.zb.ecommerce.model.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto implements Serializable {
  private String name;
  private String code;
  private CategoryType categoryType;
  private String description;
  private Long price;
  private String image;
  private List<ProductDetailDto> details;


  public static ProductDto from(Product product) {
    return ProductDto.builder()
            .name(product.getName())
            .code(product.getCode())
            .description(product.getDescription())
            .price(product.getPrice())
            .categoryType(product.getCategoryType())
            .image(product.getImageUrl())
            .details(product.getDetails().stream().map(ProductDetailDto::from).toList())
            .build();
  }

  public static ProductDto fromWithoutDetail(Product product) {
    return ProductDto.builder()
            .name(product.getName())
            .code(product.getCode())
            .description(product.getDescription())
            .price(product.getPrice())
            .categoryType(product.getCategoryType())
            .image(product.getImageUrl())
            .build();

  }
}
