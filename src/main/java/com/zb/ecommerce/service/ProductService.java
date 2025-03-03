package com.zb.ecommerce.service;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zb.ecommerce.domain.dto.ProductDetailDto;
import com.zb.ecommerce.domain.dto.ProductDto;
import com.zb.ecommerce.domain.form.ProductAddForm;
import com.zb.ecommerce.domain.form.ProductDetailAddForm;
import com.zb.ecommerce.domain.form.ProductDetailUpdateForm;
import com.zb.ecommerce.domain.form.ProductUpdateForm;
import com.zb.ecommerce.domain.type.CategoryType;
import com.zb.ecommerce.exception.CustomException;
import com.zb.ecommerce.exception.ErrorCode;
import com.zb.ecommerce.model.Product;
import com.zb.ecommerce.model.ProductDetail;
import com.zb.ecommerce.repository.ProductDetailRepository;
import com.zb.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.zb.ecommerce.model.QProduct.product;

@Service
@RequiredArgsConstructor
public class ProductService {

  private final JPAQueryFactory queryFactory;
  private final ProductRepository productRepository;
  private final ProductDetailRepository productDetailRepository;

  public void addProduct(ProductAddForm form) {
    boolean isExist = productRepository.existsByCode(form.getCode());

    if (isExist) {
      throw new CustomException(ErrorCode.ALREADY_ADDED_PRODUCT);
    }

    productRepository.save(Product.from(form));
  }

  public void addProductDetail(ProductDetailAddForm form) {
    Product product = findProductByCode(form.getCode());

    if (product.getDetails() != null) {
      List<ProductDetail> details = product.getDetails();
      for (ProductDetail detail : details) {
        if (detail.getSize().equals(form.getSize().toUpperCase())) {
          throw new CustomException(ErrorCode.ALREADY_ADDED_SIZE);
        }
      }
    }

    productDetailRepository.save(ProductDetail.from(form, product));
  }

  public ProductDto getProductDetail(String code) {
    Product product = findProductByCode(code);
    return ProductDto.from(product);
  }

  public List<String> getAllSearchProduct(int page, String keyword,
                                          String category, String sortType, boolean asc) {

    List<Product> products;
    OrderSpecifier<?> sort = product.name.asc();
    if (sortType.equals("price") && asc) {
      sort = product.price.asc();
    } else if (sortType.equals("price")) {
      sort = product.price.desc();
    } else if (!asc) {
      sort = product.name.desc();
    }

    CategoryType categoryType = CategoryType.fromString(category);

    if (!keyword.isEmpty() && categoryType != CategoryType.OTHERS) {
      products = queryFactory.selectFrom(product)
              .where(product.name.contains(keyword))
              .where(product.description.contains(keyword))
              .where(product.categoryType.eq(categoryType))
              .orderBy(sort)
              .limit(20)
              .offset(page)
              .fetch();
    } else if (!keyword.isEmpty()) {
      products = queryFactory.selectFrom(product)
              .where(product.name.contains(keyword))
              .where(product.description.contains(keyword))
              .orderBy(sort)
              .limit(20)
              .offset(page)
              .fetch();
    } else if (categoryType != CategoryType.OTHERS) {
      products = queryFactory.selectFrom(product)
              .where(product.categoryType.eq(categoryType))
              .orderBy(sort)
              .limit(20)
              .offset(page)
              .fetch();
    } else {
      products = queryFactory.selectFrom(product)
              .orderBy(sort)
              .limit(20)
              .offset(page)
              .fetch();
    }

    return products.stream().map(product ->
            String.format("%s : %,d원", product.getName(), product.getPrice())).toList();
  }

  @Transactional
  public ProductDto updateProduct(ProductUpdateForm form) {
    Product product = findProductByCode(form.getCode());
    setProductFromForm(form, product);
    return ProductDto.from(product);
  }

  @Transactional
  public ProductDetailDto updateProductDetail(ProductDetailUpdateForm form) {
    Product product = findProductByCode(form.getCode());
    ProductDetail detail = findProductDetailByProductAndSize(product, form.getSize());

    setProductDetailFromForm(form, detail, product);
    return ProductDetailDto.from(detail);
  }

  @Transactional
  public ProductDto deleteProduct(String code) {
    Product product = findProductByCode(code);
    productRepository.deleteByCode(code);
    return ProductDto.from(product);
  }

  @Transactional
  public ProductDetailDto deleteProductDetail(ProductDetailUpdateForm form) {
    Product product = findProductByCode(form.getCode());
    ProductDetail detail = findProductDetailByProductAndSize(product, form.getSize());
    productDetailRepository.delete(detail);
    return ProductDetailDto.from(detail);
  }

  private void setProductFromForm(ProductUpdateForm form, Product product) {
    if (form.getName() != null && !productRepository.existsByName(form.getName())) {
      product.setName(form.getName());
    }
    if (form.getChangeCode() != null && !productRepository.existsByCode(form.getCode())) {
      product.setCode(form.getChangeCode());
    }
    if (form.getDescription() != null) {
      product.setDescription(form.getDescription());
    }
    if (form.getPrice() != null) {
      product.setPrice(Long.valueOf(form.getPrice()));
    }
    if (form.getCategoryType() != null) {
      product.setCategoryType(CategoryType.fromString(form.getCategoryType()));
    }
  }

  private void setProductDetailFromForm(ProductDetailUpdateForm form, ProductDetail detail, Product product) {
    if (form.getChangeSize() != null) {
      for (ProductDetail productDetail : product.getDetails()) {
        if (productDetail.getSize().equals(form.getChangeSize())) {
          throw new CustomException(ErrorCode.ALREADY_ADDED_SIZE);
        }
      }
      detail.setSize(form.getChangeSize().toUpperCase());
    }
    if (form.getQuantity() != null) {
      detail.setQuantity(Integer.valueOf(form.getQuantity()));
    }
  }

  private ProductDetail findProductDetailByProductAndSize(Product product, String size) {
    return product.getDetails().stream()
            .filter(detail -> detail.getSize().equals(size.toUpperCase()))
            .findFirst()
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_SIZE));
  }

  private Product findProductByCode(String code) {
    return productRepository.findByCode(code)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PRODUCT));
  }
}
