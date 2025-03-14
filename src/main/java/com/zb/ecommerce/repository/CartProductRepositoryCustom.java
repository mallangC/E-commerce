package com.zb.ecommerce.repository;

import com.zb.ecommerce.domain.dto.CartProductDto;
import com.zb.ecommerce.model.CartProduct;
import org.springframework.data.domain.Page;

public interface CartProductRepositoryCustom {
  CartProduct searchCartProduct(Long id);

  Page<CartProductDto> searchCartProducts(int page, String email);
}
