package com.zb.ecommerce.repository;

import com.zb.ecommerce.domain.type.CategoryType;
import com.zb.ecommerce.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
  Page<Product> findAll(Pageable pageable);

  Page<Product> findAllByCategoryType(CategoryType categoryType, Pageable pageable);

  Optional<Product> findByCode(String code);

  boolean existsByCode(String code);

  Page<Product> findByNameContainingOrDescriptionContaining(String name, String description, Pageable pageable);

  boolean existsByName(String name);

  void deleteByCode(String code);
}
