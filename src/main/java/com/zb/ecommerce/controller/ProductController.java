package com.zb.ecommerce.controller;

import com.zb.ecommerce.domain.dto.PageDto;
import com.zb.ecommerce.domain.dto.ProductDetailDto;
import com.zb.ecommerce.domain.dto.ProductDto;
import com.zb.ecommerce.domain.form.ProductAddForm;
import com.zb.ecommerce.domain.form.ProductDetailAddForm;
import com.zb.ecommerce.domain.form.ProductDetailUpdateForm;
import com.zb.ecommerce.domain.form.ProductUpdateForm;
import com.zb.ecommerce.domain.type.CategoryType;
import com.zb.ecommerce.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class ProductController {

  private final ProductService productService;

  @PostMapping("/product")
  public ResponseEntity<String> addProduct(@Valid @RequestBody ProductAddForm form) {
    productService.addProduct(form);
    return ResponseEntity.ok(form.getName() + " 상품이 추가되었습니다.");
  }

  @PostMapping("/product/detail")
  public ResponseEntity<String> addProductDetail(@Valid @RequestBody ProductDetailAddForm form) {
    productService.addProductDetail(form);
    return ResponseEntity.ok(form.getCode() + " 의 세부사항이 추가되었습니다.");
  }

  @GetMapping("/products/detail")
  public ResponseEntity<ProductDto> getProductDetail(@RequestParam String code) {
    return ResponseEntity.ok(productService.getProductDetail(code));
  }

  @GetMapping("/products/search")
  public ResponseEntity<PageDto> getAllSearchProduct(
          @RequestParam(defaultValue = "0") int page,
          @RequestParam(required = false) String keyword,
          @RequestParam(required = false) CategoryType category,
          @RequestParam(defaultValue = "name") String sort,
          @RequestParam(defaultValue = "true") boolean asc) {
    return ResponseEntity.ok(productService.getAllSearchProduct(
            page, keyword, category, sort, asc));
  }

  @PatchMapping("/product")
  public ResponseEntity<ProductDto> updateProduct(
          @Valid @RequestBody ProductUpdateForm form) {
    return ResponseEntity.ok(productService.updateProduct(form));
  }

  @PatchMapping("/product/detail")
  public ResponseEntity<ProductDetailDto> updateProductDetail(
          @Valid @RequestBody ProductDetailUpdateForm form) {
    return ResponseEntity.ok(productService.updateProductDetail(form));
  }

  @DeleteMapping("/product")
  public ResponseEntity<ProductDto> deleteProduct(@RequestParam String code) {
    return ResponseEntity.ok(productService.deleteProduct(code));
  }

  @DeleteMapping("/product/detail")
  public ResponseEntity<ProductDetailDto> deleteProductDetail(
          @Valid @RequestBody ProductDetailUpdateForm form) {
    return ResponseEntity.ok(productService.deleteProductDetail(form));
  }


}
