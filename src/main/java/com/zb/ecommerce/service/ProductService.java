package com.zb.ecommerce.service;

import com.zb.ecommerce.domain.dto.PageDto;
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

import static com.zb.ecommerce.exception.ErrorCode.NOT_FOUND_SIZE;

@Service
@RequiredArgsConstructor
public class ProductService {

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
    Product product = productRepository.searchByCode(form.getCode());

    if (!product.getDetails().isEmpty()) {
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
    Product product = productRepository.searchByCode(code);
    return ProductDto.from(product);
  }

  public PageDto getAllSearchProduct(int page,
                                     String keyword,
                                     CategoryType category,
                                     String sortType,
                                     boolean asc) {

    return PageDto.from(productRepository.searchAll(page, keyword, category, sortType, asc));
  }

  @Transactional
  public ProductDto updateProduct(ProductUpdateForm form) {
    Product product = productRepository.searchByCode(form.getCode());
    setProductFromForm(form, product);
    return ProductDto.from(product);
  }

  @Transactional
  public ProductDetailDto updateProductDetail(ProductDetailUpdateForm form) {
    Product product = productRepository.searchByCode(form.getCode());
    ProductDetail productDetail = product.getDetails().stream()
            .filter(detail -> detail.getSize().equals(form.getSize().toUpperCase()))
            .findFirst().orElse(null);

    if (productDetail == null) {
      throw new CustomException(NOT_FOUND_SIZE);
    }

    setProductDetailFromForm(form, productDetail, product);
    return ProductDetailDto.from(productDetail);
  }

  @Transactional
  public ProductDto deleteProduct(String code) {
    Product product = productRepository.searchByCode(code);
    productRepository.deleteByCode(code);
    return ProductDto.from(product);
  }

  @Transactional
  public ProductDetailDto deleteProductDetail(ProductDetailUpdateForm form) {
    Product product = productRepository.searchByCode(form.getCode());
    ProductDetail productDetail = product.getDetails().stream()
            .filter(detail -> detail.getSize().equals(form.getSize().toUpperCase()))
            .findFirst().orElse(null);

    if (productDetail == null) {
      throw new CustomException(NOT_FOUND_SIZE);
    }

    productDetailRepository.delete(productDetail);
    return ProductDetailDto.from(productDetail);
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
      product.setCategoryType(form.getCategoryType());
    }
  }

  private void setProductDetailFromForm(ProductDetailUpdateForm form,
                                        ProductDetail detail,
                                        Product product) {
    if (form.getChangeSize() != null) {
      for (ProductDetail productDetail : product.getDetails()) {
        if (productDetail.getSize().equals(form.getChangeSize())) {
          throw new CustomException(ErrorCode.ALREADY_ADDED_SIZE);
        }
      }
      detail.setSize(form.getChangeSize().toUpperCase());
    }
    detail.setQuantity(form.getQuantity());
  }

}
