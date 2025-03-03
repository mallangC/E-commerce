package com.zb.ecommerce.repository;

import com.zb.ecommerce.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> , ProductRepositoryCustom{

  boolean existsByCode(String code);

  boolean existsByName(String name);

  void deleteByCode(String code);

}
