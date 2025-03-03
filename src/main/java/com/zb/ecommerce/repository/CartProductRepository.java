package com.zb.ecommerce.repository;

import com.zb.ecommerce.model.CartProduct;
import com.zb.ecommerce.model.Member;
import com.zb.ecommerce.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartProductRepository extends JpaRepository<CartProduct, Long> {

  Optional<CartProduct> findByMemberAndProductAndSize(Member member, Product product, String size);

}
