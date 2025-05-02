package com.zb.ecommerce.repository;

import com.zb.ecommerce.model.CartProduct;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface CartProductRepositoryCustom {
  Optional<CartProduct> searchCartProductById(Long id);

  Page<CartProduct> searchCartProductsByEmail(int page, String email);
}
