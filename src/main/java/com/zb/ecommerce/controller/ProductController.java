package com.zb.ecommerce.controller;

import com.zb.ecommerce.domain.dto.ProductDetailDto;
import com.zb.ecommerce.domain.dto.ProductDto;
import com.zb.ecommerce.domain.form.ProductAddForm;
import com.zb.ecommerce.domain.form.ProductDetailAddForm;
import com.zb.ecommerce.domain.form.ProductDetailUpdateForm;
import com.zb.ecommerce.domain.form.ProductUpdateForm;
import com.zb.ecommerce.domain.type.CategoryType;
import com.zb.ecommerce.response.HttpApiResponse;
import com.zb.ecommerce.response.PaginatedResponse;
import com.zb.ecommerce.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/product")
public class ProductController {

  private final ProductService productService;

  @PostMapping
  public ResponseEntity<HttpApiResponse<String>> addProduct(@Valid @RequestBody ProductAddForm form) {
    productService.addProduct(form);
    return ResponseEntity.ok(new HttpApiResponse<>(
            form.getName() + " 상품이 추가되었습니다.",
            "상품 추가 성공",
            HttpStatus.OK));
  }

  @PostMapping("/image")
  public ResponseEntity<HttpApiResponse<String>> addProductImage(MultipartFile file) throws IOException {
    return ResponseEntity.ok(new HttpApiResponse<>(
            productService.addProductImage(file),
            "이미지 등록 성공",
            HttpStatus.OK));
  }

  @PostMapping("/detail")
  public ResponseEntity<HttpApiResponse<ProductDetailDto>> addProductDetail(
          @Valid @RequestBody ProductDetailAddForm form) {
    return ResponseEntity.ok(new HttpApiResponse<>(
            productService.addProductDetail(form),
            "상품 상세정보 추가 성공",
            HttpStatus.OK));
  }

  @GetMapping("/detail")
  public ResponseEntity<HttpApiResponse<ProductDto>> getProductDetail(@RequestParam String code) {
    return ResponseEntity.ok(new HttpApiResponse<>(
            productService.getProductDetail(code),
            "상품 상세정보 조회 성공",
            HttpStatus.OK));
  }

  @GetMapping("/search")
  public ResponseEntity<PaginatedResponse<ProductDto>> getAllSearchProduct(
          @RequestParam(defaultValue = "0") int page,
          @RequestParam(required = false) String keyword,
          @RequestParam(required = false) CategoryType category,
          @RequestParam(defaultValue = "name") String sort,
          @RequestParam(defaultValue = "true") boolean asc) {
    return ResponseEntity.ok(PaginatedResponse.from(
            productService.getAllSearchProduct(page, keyword, category, sort, asc),
            "상품 전체 조회 성공",
            HttpStatus.OK));
  }

  @PatchMapping
  public ResponseEntity<HttpApiResponse<ProductDto>> updateProduct(
          @Valid @RequestBody ProductUpdateForm form) {
    return ResponseEntity.ok(new HttpApiResponse<>(
            productService.updateProduct(form),
            "상품 수정 성공",
            HttpStatus.OK));
  }

  @PatchMapping("/detail")
  public ResponseEntity<HttpApiResponse<ProductDetailDto>> updateProductDetail(
          @Valid @RequestBody ProductDetailUpdateForm form) {
    return ResponseEntity.ok(new HttpApiResponse<>(
            productService.updateProductDetail(form),
            "상품 상세정보 수정 성공",
            HttpStatus.OK));
  }

  @DeleteMapping
  public ResponseEntity<HttpApiResponse<ProductDto>> deleteProduct(@RequestParam String code) {
    return ResponseEntity.ok(new HttpApiResponse<>(
            productService.deleteProduct(code),
            "상품 삭제 성공",
            HttpStatus.OK));
  }

  @DeleteMapping("/detail")
  public ResponseEntity<HttpApiResponse<ProductDetailDto>> deleteProductDetail(
          @Valid @RequestBody ProductDetailUpdateForm form) {
    return ResponseEntity.ok(new HttpApiResponse<>(
            productService.deleteProductDetail(form),
            "상품 상세정보 삭제 성공",
            HttpStatus.OK));
  }


}
