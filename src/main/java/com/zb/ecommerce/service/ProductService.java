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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

  private final ProductRepository productRepository;
  private final ProductDetailRepository productDetailRepository;

  @CacheEvict(value = {"product","products"}, allEntries = true)
  public void addProduct(ProductAddForm form) {
    boolean isExist = productRepository.existsByCode(form.getCode());

    if (isExist) {
      throw new CustomException(ErrorCode.ALREADY_ADDED_PRODUCT);
    }

    productRepository.save(Product.from(form));
  }

  @CacheEvict(value = {"product","products"}, allEntries = true)
  public void addProductDetail(ProductDetailAddForm form) {
    Product product = findProductByCode(form.getCode());

    if (product.getDetails() != null){
      List<ProductDetail> details = product.getDetails();
      for (ProductDetail detail : details) {
        if (detail.getSize().equals(form.getSize().toUpperCase())) {
          throw new CustomException(ErrorCode.ALREADY_ADDED_SIZE);
        }
      }
    }

    productDetailRepository.save(ProductDetail.from(form, product));
  }

  @Cacheable(value = "products", key = "'all-'+#page")
  public List<String> getAllProduct(int page) {
    Sort sort = Sort.by(Sort.Direction.ASC, "name");
    Pageable pageableSetup = PageRequest.of(page, 20, sort);
    Page<Product> productPage = productRepository.findAll(pageableSetup);

    if (productPage.isEmpty()) {
      return null;
    }

    List<String> productNames = productPage.getContent().stream()
            .filter(product -> product.getDetails().stream().anyMatch(detail -> detail.getQuantity() > 0))
            .map(product -> String.format(String.format("%s : %,d", product.getName(), product.getPrice())))
            .collect(Collectors.toCollection(ArrayList::new));
    if (productNames.size() == productPage.getContent().size()) {
      return productNames;
    }
    productNames.addAll(productPage.getContent().stream()
            .filter(product -> product.getDetails().stream().allMatch(detail -> detail.getQuantity() == 0))
            .map(product -> String.format(String.format("(품절)%s : %,d", product.getName(), product.getPrice())))
            .toList());

    return productNames;
  }

  @Cacheable(value = "product", key = "'product-detail-'+#code")
  public ProductDto getProductDetail(String code) {
    Product product = findProductByCode(code);
    return ProductDto.from(product);
  }

  @Cacheable(value = "products", key = "'all-'+#type+'-'+#page+'-'+#asc")
  public List<String> getAllProductSort(int page, String type, boolean asc) {
      type = type.toLowerCase();
    if (!type.equals("price") && !type.equals("name")) {
      type = "name";
    }
    Sort sort = Sort.by(Sort.Direction.ASC, type);
    if (!asc) {
      sort = Sort.by(Sort.Direction.DESC, type);
    }
    Pageable pageableSetup = PageRequest.of(page, 20, sort);
    Page<Product> productPage = productRepository.findAll(pageableSetup);
    return productPage.getContent().stream()
            .map(product -> String.format("%s : %,d원", product.getName(), product.getPrice()))
            .toList();
  }

  @Cacheable(value = "products", key = "'all-category-'+#page+'-'+#category")
  public List<String> getAllProductSortCategory(int page, String category) {
    Sort sort = Sort.by(Sort.Direction.ASC, "name");
    Pageable pageableSetup = PageRequest.of(page, 20, sort);

    Page<Product> productPage = productRepository.findAllByCategoryType(
            CategoryType.fromString(category), pageableSetup);

    return productPage.getContent().stream()
            .map(product -> String.format("%s : %,d원", product.getName(), product.getPrice()))
            .toList();
  }

  @Cacheable(value = "products", key = "'all-keyword-'+#page+'-'+#keyword")
  public List<String> getAllProductSearchKeyword(int page, String keyword) {
    Sort sort = Sort.by(Sort.Direction.ASC, "name");
    Pageable pageableSetup = PageRequest.of(page, 20, sort);
    Page<Product> productPage = productRepository.findByNameContainingOrDescriptionContaining(
            keyword, keyword, pageableSetup);
    return productPage.getContent().stream()
            .map(product -> String.format("%s : %,d원", product.getName(), product.getPrice()))
            .toList();
  }

  @CacheEvict(value = {"product","products"}, allEntries = true)
  @Transactional
  public ProductDto updateProduct(ProductUpdateForm form) {
    Product product = findProductByCode(form.getCode());
    setProductFromForm(form, product);
    return ProductDto.from(product);
  }

  @CacheEvict(value = {"product","products"}, allEntries = true)
  @Transactional
  public ProductDetailDto updateProductDetail(ProductDetailUpdateForm form) {
    Product product = findProductByCode(form.getCode());
    ProductDetail detail = findProductDetailByProductAndSize(product, form.getSize());

    setProductDetailFromForm(form, detail, product);
    return ProductDetailDto.from(detail);
  }

  @CacheEvict(value = {"product","products"}, allEntries = true)
  @Transactional
  public ProductDto deleteProduct(String code) {
    Product product = findProductByCode(code);
    productRepository.deleteByCode(code);
    return ProductDto.from(product);
  }

  @CacheEvict(value = {"product","products"}, allEntries = true)
  @Transactional
  public ProductDetailDto deleteProductDetail(ProductDetailUpdateForm form) {
    Product product = findProductByCode(form.getCode());
    ProductDetail detail = findProductDetailByProductAndSize(product, form.getSize());
    productDetailRepository.delete(detail);
    return ProductDetailDto.from(detail);
  }

  private void setProductFromForm(ProductUpdateForm form, Product product) {
    if (form.getName() != null && !productRepository.existsByName(form.getName())){
      product.setName(form.getName());
    }
    if (form.getChangeCode() != null && !productRepository.existsByCode(form.getCode())){
      product.setCode(form.getChangeCode());
    }
    if (form.getDescription() != null){
      product.setDescription(form.getDescription());
    }
    if (form.getPrice() != null){
      product.setPrice(Long.valueOf(form.getPrice()));
    }
    if (form.getCategoryType() != null){
      product.setCategoryType(CategoryType.fromString(form.getCategoryType()));
    }
  }

  private void setProductDetailFromForm(ProductDetailUpdateForm form, ProductDetail detail, Product product) {
    if (form.getChangeSize() != null){
      for (ProductDetail productDetail : product.getDetails()){
        if (productDetail.getSize().equals(form.getChangeSize())){
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
            .orElseThrow(()-> new CustomException(ErrorCode.NOT_FOUND_SIZE));
  }

  private Product findProductByCode(String code) {
    return productRepository.findByCode(code)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PRODUCT));
  }
}
