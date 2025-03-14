package com.zb.ecommerce.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zb.ecommerce.domain.dto.ProductDto;
import com.zb.ecommerce.domain.type.CategoryType;
import com.zb.ecommerce.exception.CustomException;
import com.zb.ecommerce.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.zb.ecommerce.exception.ErrorCode.NOT_FOUND_PRODUCT;
import static com.zb.ecommerce.model.QProduct.product;
import static com.zb.ecommerce.model.QProductDetail.productDetail;

@RequiredArgsConstructor
public class ProductRepositoryCustomImpl implements ProductRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  @Override
  public Page<ProductDto> searchAll(int page,
                                    String keyword,
                                    CategoryType category,
                                    String sortType,
                                    boolean asc) {

    Pageable pageable = PageRequest.of(page, 20);

    OrderSpecifier<?> querySort = product.name.asc();
    if (sortType.equals("price") && asc) {
      querySort = product.price.asc();
    } else if (sortType.equals("price")) {
      querySort = product.price.desc();
    } else if (!asc) {
      querySort = product.name.desc();
    }

    List<Product> products = queryFactory.selectFrom(product)
            .where(keyword != null && !keyword.isEmpty() ?
                    product.name.contains(keyword).or(product.description.contains(keyword))
                    : null)
            .where(category != null ? product.categoryType.eq(category) : null)
            .leftJoin(product.details, productDetail)
            .orderBy(querySort)
            .limit(pageable.getPageSize())
            .offset(pageable.getOffset())
            .fetch();

    long total = products.size();

    List<ProductDto> productDtoList = products.stream()
            .map(ProductDto::fromWithoutDetail)
            .toList();

    return new PageImpl<>(productDtoList, pageable, total);
  }


  @Override
  public Product searchByCode(String code){
    Product searchProduct = queryFactory
            .selectFrom(product)
            .leftJoin(product.details, productDetail).fetchJoin()
            .where(product.code.eq(code))
            .fetchOne();

    if (searchProduct == null) {
      throw new CustomException(NOT_FOUND_PRODUCT);
    }

    return searchProduct;
  }



}
