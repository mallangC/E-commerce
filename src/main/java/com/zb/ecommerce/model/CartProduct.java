package com.zb.ecommerce.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartProduct {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @ManyToOne
  @JoinColumn(name = "member_id")
  private Member member;
  @ManyToOne
  @JoinColumn(name = "product_id")
  private Product product;
  private String size;
  private Integer quantity;

  public void changeQuantity(Integer quantity) {
    this.quantity = quantity;
  }
  public void addQuantity(Integer quantity) {
    this.quantity += quantity;
  }
}
