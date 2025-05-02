package com.zb.ecommerce.repository;

import com.zb.ecommerce.domain.type.CategoryType;
import com.zb.ecommerce.model.Product;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface ProductRepositoryCustom {
  Page<Product> searchAllProduct(int page, String keyword,
                                    CategoryType category, String sortType, boolean asc);

  Optional<Product> searchProductByCode(String code);

}
