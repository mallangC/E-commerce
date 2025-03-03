package com.zb.ecommerce.repository;

import com.zb.ecommerce.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

  Optional<Product> findByCode(String code);

  boolean existsByCode(String code);

  boolean existsByName(String name);

  void deleteByCode(String code);

}
