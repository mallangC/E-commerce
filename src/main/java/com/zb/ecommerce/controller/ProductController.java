package com.zb.ecommerce.controller;

import com.zb.ecommerce.domain.dto.ProductDetailDto;
import com.zb.ecommerce.domain.dto.ProductDto;
import com.zb.ecommerce.domain.form.ProductAddForm;
import com.zb.ecommerce.domain.form.ProductDetailAddForm;
import com.zb.ecommerce.domain.form.ProductDetailUpdateForm;
import com.zb.ecommerce.domain.form.ProductUpdateForm;
import com.zb.ecommerce.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

  @GetMapping("/products")
  public ResponseEntity<List<String>> getAllProduct(@RequestParam(defaultValue = "0") int page) {
    return ResponseEntity.ok(productService.getAllProduct(page));
  }

  @GetMapping("/products/detail")
  public ResponseEntity<ProductDto> getProductDetail(@RequestParam String code) {
    return ResponseEntity.ok(productService.getProductDetail(code));
  }

  @GetMapping("/products/sort/name")
  public ResponseEntity<List<String>> getProductSortName(
          @RequestParam(defaultValue = "0") int page,
          @RequestParam(defaultValue = "true") boolean asc) {
    return ResponseEntity.ok(productService.getAllProductSort( page, "name", asc));
  }


  @GetMapping("/products/sort/price")
  public ResponseEntity<List<String>> getProductSortPrice(
          @RequestParam(defaultValue = "0") int page,
          @RequestParam(defaultValue = "true") boolean asc) {
    return ResponseEntity.ok(productService.getAllProductSort( page, "price", asc));
  }

  @GetMapping("/products/sort/category")
  public ResponseEntity<List<String>> getProductSortCategory(
          @RequestParam(defaultValue = "0") int page,
          @RequestParam(defaultValue = "other") String category) {
    return ResponseEntity.ok(productService.getAllProductSortCategory(page, category));
  }

  @GetMapping("/products/search/keyword")
  public ResponseEntity<List<String>> getProductSortKeyword(
          @RequestParam(defaultValue = "0") int page,
          @RequestParam String keyword) {
    return ResponseEntity.ok(productService.getAllProductSearchKeyword(page, keyword));
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
