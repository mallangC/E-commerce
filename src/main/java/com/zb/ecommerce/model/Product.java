package com.zb.ecommerce.model;

import com.zb.ecommerce.domain.form.ProductAddForm;
import com.zb.ecommerce.domain.form.ProductUpdateForm;
import com.zb.ecommerce.domain.type.CategoryType;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(unique = true)
  private String name;
  @Column(unique = true)
  private String code;
  @Enumerated(EnumType.STRING)
  private CategoryType categoryType;
  private String description;
  private Long price;
  private String imageUrl;
  @OneToMany(mappedBy = "product", cascade = CascadeType.REMOVE, orphanRemoval = true)
  private List<ProductDetail> details;

  public static Product from(ProductAddForm form) {
    return Product.builder()
            .name(form.getName())
            .code(form.getCode())
            .categoryType(form.getCategory())
            .description(form.getDescription())
            .price(form.getPrice())
            .imageUrl(form.getImage())
            .build();
  }

  public void productUpdate(ProductUpdateForm form, boolean isExistName, boolean isExistCode) {
    if (form.getName() != null && !isExistName) {
      this.name = form.getName();
    }
    if (form.getChangeCode() != null && !isExistCode) {
      this.code = form.getChangeCode();
    }
    if (form.getDescription() != null) {
      this.description = form.getDescription();
    }
    if (form.getPrice() != null) {
      this.price = form.getPrice();
    }
    if (form.getCategoryType() != null) {
      this.categoryType = form.getCategoryType();
    }
    if (form.getImage() != null) {
      this.imageUrl = form.getImage();
    }
  }
}
