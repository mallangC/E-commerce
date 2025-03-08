package com.zb.ecommerce.model;

import com.zb.ecommerce.domain.form.ProductAddForm;
import com.zb.ecommerce.domain.type.CategoryType;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
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
  @OneToMany(mappedBy = "product", cascade = CascadeType.REMOVE, orphanRemoval = true)
  private List<ProductDetail> details;

  public static Product from(ProductAddForm form) {

    return Product.builder()
            .name(form.getName())
            .code(form.getCode())
            .categoryType(form.getCategory())
            .description(form.getDescription())
            .price(Long.valueOf(form.getPrice()))
            .details(new ArrayList<>())
            .build();
  }
}
