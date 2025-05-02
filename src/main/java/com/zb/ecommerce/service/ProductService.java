package com.zb.ecommerce.service;

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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static com.zb.ecommerce.exception.ErrorCode.NOT_FOUND_SIZE;

@Service
@RequiredArgsConstructor
public class ProductService {

  private final ProductRepository productRepository;
  private final ProductDetailRepository productDetailRepository;
  private final S3Service s3Service;

  public void addProduct(ProductAddForm form) {
    boolean isExist = productRepository.existsByCode(form.getCode());
    if (isExist) {
      throw new CustomException(ErrorCode.ALREADY_ADDED_PRODUCT);
    }
    productRepository.save(Product.from(form));
  }

  public String addProductImage(MultipartFile file) throws IOException {
    return s3Service.uploadFile(file.getInputStream(), file.getOriginalFilename(), file.getContentType());
  }

  public ProductDetailDto addProductDetail(ProductDetailAddForm form) {
    Product product = searchProductByCodeAndValidation(form.getCode());
    if (!product.getDetails().isEmpty()) {
      List<ProductDetail> details = product.getDetails();
      for (ProductDetail detail : details) {
        if (detail.getSize().equals(form.getSize().toUpperCase())) {
          throw new CustomException(ErrorCode.ALREADY_ADDED_SIZE);
        }
      }
    }
    ProductDetail productDetail = ProductDetail.from(form, product);
    return ProductDetailDto.from(productDetailRepository.save(productDetail));
  }

  public ProductDto getProductDetail(String code) {
    Product product = searchProductByCodeAndValidation(code);
    return ProductDto.from(product);
  }

  public Page<ProductDto> getAllSearchProduct(int page,
                                              String keyword,
                                              CategoryType category,
                                              String sortType,
                                              boolean asc) {

    Page<Product> products = productRepository.searchAllProduct(page, keyword, category, sortType, asc);

    List<ProductDto> productDtoList = products.stream()
            .map(ProductDto::fromWithoutDetail)
            .toList();

    return new PageImpl<>(productDtoList, products.getPageable(),
            products.getTotalElements());
  }

  @Transactional
  public ProductDto updateProduct(ProductUpdateForm form) {
    Product product = searchProductByCodeAndValidation(form.getCode());
    if (form.getImage() != null) {
      s3Service.deleteFile(form.getImage());
    }
    boolean isExistName = productRepository.existsByName(form.getName());
    boolean isExistCode = productRepository.existsByCode(form.getCode());
    product.productUpdate(form, isExistName, isExistCode);
    return ProductDto.from(product);
  }

  @Transactional
  public ProductDetailDto updateProductDetail(ProductDetailUpdateForm form) {
    Product product = searchProductByCodeAndValidation(form.getCode());
    ProductDetail productDetail = productDetailValidation(product, form.getSize());
    if (form.getChangeSize() != null) {
      for (ProductDetail detail : product.getDetails()) {
        if (detail.getSize().equals(form.getChangeSize())) {
          throw new CustomException(ErrorCode.ALREADY_ADDED_SIZE);
        }
      }
    }
    productDetail.productDetailUpdate(form);
    return ProductDetailDto.from(productDetail);
  }

  @Transactional
  public ProductDto deleteProduct(String code) {
    Product product = searchProductByCodeAndValidation(code);
    s3Service.deleteFile(product.getImageUrl());
    productRepository.deleteByCode(code);
    return ProductDto.from(product);
  }

  @Transactional
  public ProductDetailDto deleteProductDetail(ProductDetailUpdateForm form) {
    Product product = searchProductByCodeAndValidation(form.getCode());
    ProductDetail productDetail = productDetailValidation(product, form.getSize());
    productDetailRepository.delete(productDetail);
    return ProductDetailDto.from(productDetail);
  }

  private Product searchProductByCodeAndValidation(String code) {
    return productRepository.searchProductByCode(code)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PRODUCT));
  }

  private ProductDetail productDetailValidation(Product product, String size) {
    ProductDetail productDetail = product.getDetails().stream()
            .filter(detail -> detail.getSize().equals(size.toUpperCase()))
            .findFirst().orElse(null);
    if (productDetail == null) {
      throw new CustomException(NOT_FOUND_SIZE);
    }
    return productDetail;
  }

}
