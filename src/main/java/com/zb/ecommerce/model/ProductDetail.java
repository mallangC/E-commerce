package com.zb.ecommerce.model;

import com.zb.ecommerce.domain.form.ProductDetailAddForm;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
public class ProductDetail extends BaseEntity{

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @ManyToOne
  @JoinColumn(name = "product_id")
  private Product product;
  @Column(unique = true)
  private String size;
  private Integer quantity;

  public static ProductDetail from(ProductDetailAddForm form, Product product) {
    return ProductDetail.builder()
            .product(product)
            .size(form.getSize().toUpperCase())
            .quantity(Integer.parseInt(form.getQuantity()))
            .build();
  }
}
