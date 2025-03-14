package com.zb.ecommerce.controller;

import com.zb.ecommerce.domain.dto.CartProductDto;
import com.zb.ecommerce.domain.dto.PageDto;
import com.zb.ecommerce.domain.form.CartAddForm;
import com.zb.ecommerce.domain.form.CartUpdateForm;
import com.zb.ecommerce.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CartController {

  private final CartService cartService;

  @PostMapping("/cart")
  public ResponseEntity<CartProductDto> addCartProduct(@Valid @RequestBody CartAddForm form) {
    String email = SecurityContextHolder.getContext().getAuthentication().getName();
    return ResponseEntity.ok(cartService.addProductToCart(email, form));
  }

  @GetMapping("/carts")
  public ResponseEntity<PageDto<CartProductDto>> getCartProducts(@RequestParam int page) {
    String email = SecurityContextHolder.getContext().getAuthentication().getName();
    return ResponseEntity.ok(cartService.getAllCartProducts(page, email));
  }

  @PatchMapping("/cart")
  public ResponseEntity<CartProductDto> updateCartProduct(@Valid @RequestBody CartUpdateForm form) {
    return ResponseEntity.ok(cartService.updateProductToCart(form));
  }

  @DeleteMapping("/cart")
  public ResponseEntity<String> deleteCartProduct(@RequestParam Long id) {
    cartService.deleteProductToCart(id);
    return ResponseEntity.ok("카트에 담긴 " + id + "상품이 삭제되었습니다.");
  }
}
