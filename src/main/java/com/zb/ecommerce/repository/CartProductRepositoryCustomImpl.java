package com.zb.ecommerce.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zb.ecommerce.model.CartProduct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static com.zb.ecommerce.model.QCartProduct.cartProduct;
import static com.zb.ecommerce.model.QProduct.product;
import static com.zb.ecommerce.model.QProductDetail.productDetail;

@RequiredArgsConstructor
public class CartProductRepositoryCustomImpl implements CartProductRepositoryCustom {
  private final JPAQueryFactory queryFactory;

  @Override
  public Optional<CartProduct> searchCartProductById(Long id) {
    return Optional.ofNullable(queryFactory.selectFrom(cartProduct)
            .join(cartProduct.product, product).fetchJoin()
            .join(cartProduct.product.details, productDetail).fetchJoin()
            .where(cartProduct.id.eq(id))
            .fetchOne());
  }

  @Override
  public Page<CartProduct> searchCartProductsByEmail(int page, String email) {

    Pageable pageable = PageRequest.of(page, 20);

    Long total = queryFactory.select(cartProduct.count())
            .from(cartProduct)
            .where(cartProduct.member.email.eq(email))
            .join(cartProduct.product, product)
            .join(cartProduct.product.details, productDetail)
            .fetchOne();
    if (total == null) {
      return new PageImpl<>(List.of(), pageable, 0);
    }
    List<CartProduct> cartProducts = queryFactory.selectFrom(cartProduct)
            .where(cartProduct.member.email.eq(email))
            .join(cartProduct.product, product).fetchJoin()
            .join(cartProduct.product.details, productDetail).fetchJoin()
            .limit(pageable.getPageSize())
            .offset(pageable.getOffset())
            .fetch();

    return new PageImpl<>(cartProducts, pageable, total);
  }
}
