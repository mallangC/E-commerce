package com.zb.ecommerce.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
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
}
