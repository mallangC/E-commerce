package com.zb.ecommerce.repository;

import com.zb.ecommerce.domain.dto.ProductDto;
import com.zb.ecommerce.domain.type.CategoryType;
import com.zb.ecommerce.model.Product;
import org.springframework.data.domain.Page;

public interface ProductRepositoryCustom {
  Page<ProductDto> searchAll(int page, String keyword,
                             CategoryType category, String sortType, boolean asc);

  Product searchByCode(String code);

}
