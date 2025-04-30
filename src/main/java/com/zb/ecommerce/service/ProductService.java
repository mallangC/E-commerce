package com.zb.ecommerce.service;

import com.zb.ecommerce.response.PaginatedResponse;
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

  public void addProductDetail(ProductDetailAddForm form) {
    Product product = productRepository.searchProductByCode(form.getCode());
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
    Product product = productRepository.searchProductByCode(code);
    return ProductDto.from(product);
  }

  public PaginatedResponse<ProductDto> getAllSearchProduct(int page,
                                                           String keyword,
                                                           CategoryType category,
                                                           String sortType,
                                                           boolean asc) {

    return PaginatedResponse.from(productRepository.searchAllProduct(page, keyword, category, sortType, asc));
  }

  @Transactional
  public ProductDto updateProduct(ProductUpdateForm form) {
    Product product = productRepository.searchProductByCode(form.getCode());
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
    Product product = productRepository.searchProductByCode(form.getCode());
    ProductDetail productDetail = productDetailValidation(product, form.getSize());
    if(form.getChangeSize() != null){
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
    Product product = productRepository.searchProductByCode(code);
    s3Service.deleteFile(product.getImageUrl());
    productRepository.deleteByCode(code);
    return ProductDto.from(product);
  }

  @Transactional
  public ProductDetailDto deleteProductDetail(ProductDetailUpdateForm form) {
    Product product = productRepository.searchProductByCode(form.getCode());
    ProductDetail productDetail = productDetailValidation(product, form.getSize());
    productDetailRepository.delete(productDetail);
    return ProductDetailDto.from(productDetail);
  }

  private ProductDetail productDetailValidation(Product product, String size){
    ProductDetail productDetail = product.getDetails().stream()
            .filter(detail -> detail.getSize().equals(size.toUpperCase()))
            .findFirst().orElse(null);
    if (productDetail == null) {
      throw new CustomException(NOT_FOUND_SIZE);
    }
    return productDetail;
  }

}
