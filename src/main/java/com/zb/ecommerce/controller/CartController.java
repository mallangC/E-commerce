package com.zb.ecommerce.controller;

import com.zb.ecommerce.domain.dto.CartProductDto;
import com.zb.ecommerce.domain.form.CartAddForm;
import com.zb.ecommerce.domain.form.CartUpdateForm;
import com.zb.ecommerce.response.HttpApiResponse;
import com.zb.ecommerce.response.PaginatedResponse;
import com.zb.ecommerce.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CartController {

  private final CartService cartService;

  @PostMapping("/cart")
  public ResponseEntity<HttpApiResponse<CartProductDto>> addCartProduct(
          @Valid @RequestBody CartAddForm form,
          @AuthenticationPrincipal String email) {
    return ResponseEntity.ok(new HttpApiResponse<>(
            cartService.addProductToCart(form, email),
            "카트에 상품 추가 성공",
            HttpStatus.OK));
  }

  @GetMapping("/carts")
  public ResponseEntity<PaginatedResponse<CartProductDto>> getCartProducts(
          @RequestParam int page,
          @AuthenticationPrincipal String email) {
    return ResponseEntity.ok(PaginatedResponse.from(
            cartService.getAllCartProducts(page, email),
            "카트 상품 조회 성공",
            HttpStatus.OK));
  }

  @PatchMapping("/cart")
  public ResponseEntity<HttpApiResponse<CartProductDto>> updateCartProduct
          (@Valid @RequestBody CartUpdateForm form,
           @AuthenticationPrincipal String email) {
    return ResponseEntity.ok(new HttpApiResponse<>(
            cartService.updateProductToCart(form, email),
            "카트 상품 수정 성공",
            HttpStatus.OK));
  }

  @DeleteMapping("/cart")
  public ResponseEntity<HttpApiResponse<String>> deleteCartProduct(
          @RequestParam Long id,
          @AuthenticationPrincipal String email) {
    cartService.deleteProductToCart(id, email);
    return ResponseEntity.ok(new HttpApiResponse<>(
            "카트에 담긴 " + id + " 상품이 삭제되었습니다.",
            "카트 상품 삭제 성공",
            HttpStatus.OK));
  }
}
