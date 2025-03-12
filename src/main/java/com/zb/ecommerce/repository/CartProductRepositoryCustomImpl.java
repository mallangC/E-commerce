package com.zb.ecommerce.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zb.ecommerce.domain.dto.CartProductDto;
import com.zb.ecommerce.exception.CustomException;
import com.zb.ecommerce.exception.ErrorCode;
import com.zb.ecommerce.model.CartProduct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.zb.ecommerce.model.QCartProduct.cartProduct;
import static com.zb.ecommerce.model.QProduct.product;
import static com.zb.ecommerce.model.QProductDetail.productDetail;

@RequiredArgsConstructor
public class CartProductRepositoryCustomImpl implements CartProductRepositoryCustom {
  private final JPAQueryFactory queryFactory;

  @Override
  public CartProduct searchCartProduct(Long id) {
    CartProduct searchCartProduct = queryFactory.selectFrom(cartProduct)
            .join(cartProduct.product, product).fetchJoin()
            .join(cartProduct.product.details, productDetail).fetchJoin()
            .where(cartProduct.id.eq(id))
            .fetchOne();
    if (searchCartProduct == null) {
      throw new CustomException(ErrorCode.NOT_FOUND_CART_PRODUCT);
    }
    return searchCartProduct;
  }

  @Override
  public Page<CartProductDto> searchCartProducts(int page, String email) {

    Pageable pageable = PageRequest.of(page, 20);

    List<CartProduct> cartProducts = queryFactory.selectFrom(cartProduct)
            .where(cartProduct.member.email.eq(email))
            .join(cartProduct.product, product).fetchJoin()
            .join(cartProduct.product.details, productDetail).fetchJoin()
            .limit(pageable.getPageSize())
            .offset(pageable.getOffset())
            .fetch();

    long total = cartProducts.size();

    List<CartProductDto> cartProductList = cartProducts.stream()
            .map(CartProductDto::from)
            .toList();

    return new PageImpl<>(cartProductList, pageable, total);
  }
}
