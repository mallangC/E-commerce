package com.zb.ecommerce.model;

import com.zb.ecommerce.domain.form.ProductDetailAddForm;
import com.zb.ecommerce.domain.form.ProductDetailUpdateForm;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDetail extends BaseEntity {

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
            .quantity(form.getQuantity())
            .build();
  }

  public void productDetailUpdate(ProductDetailUpdateForm form) {
    if (form.getChangeSize() != null) {
      this.size = form.getChangeSize().toUpperCase();
    }
    this.quantity = form.getQuantity();
  }
}
