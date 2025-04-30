package com.zb.ecommerce.service;

import com.zb.ecommerce.domain.dto.CartProductDto;
import com.zb.ecommerce.response.PaginatedResponse;
import com.zb.ecommerce.domain.form.CartAddForm;
import com.zb.ecommerce.domain.form.CartUpdateForm;
import com.zb.ecommerce.exception.CustomException;
import com.zb.ecommerce.exception.ErrorCode;
import com.zb.ecommerce.model.CartProduct;
import com.zb.ecommerce.model.Member;
import com.zb.ecommerce.model.Product;
import com.zb.ecommerce.model.ProductDetail;
import com.zb.ecommerce.repository.CartProductRepository;
import com.zb.ecommerce.repository.MemberRepository;
import com.zb.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CartService {

  private final MemberRepository memberRepository;
  private final CartProductRepository cartProductRepository;
  private final ProductRepository productRepository;

  @CacheEvict(value = "cart", allEntries = true)
  @Transactional
  public CartProductDto addProductToCart(String email, CartAddForm form) {
    Member member = memberRepository.searchMemberByEmail(email);
    Product product = productRepository.searchProductByCode(form.getProductCode());
    int quantity = form.getQuantity();

    String size = form.getSize().toUpperCase();
    ProductDetail productDetail = sizeCheck(product, size);

    List<CartProduct> searchCart = member.getCart().stream()
            .filter(item -> Objects.equals(item.getProduct().getId(), product.getId()))
            .toList();

    boolean isExistSize = searchCart.stream()
            .anyMatch(item -> item.getSize().equals(productDetail.getSize()));

    if (!searchCart.isEmpty() && isExistSize) {
      CartProduct cartProduct = quantityCheck(member, product, productDetail, size, quantity);
      return CartProductDto.from(cartProduct);
    }

    return CartProductDto.from(cartProductRepository.save(CartProduct.builder()
            .member(member)
            .product(product)
            .size(size)
            .quantity(quantity)
            .build()));
  }

  @Cacheable(value = "cart", key = "'cart-all-'+ #page + #email")
  public PaginatedResponse<CartProductDto> getAllCartProducts(int page, String email) {
    if (email.equals("anonymousUser")) {
      return PaginatedResponse.empty();
    }
    return PaginatedResponse.from(cartProductRepository.searchCartProducts(page, email));
  }

  @CacheEvict(value = "cart", allEntries = true)
  @Transactional
  public CartProductDto updateProductToCart(CartUpdateForm form) {
    CartProduct cartProduct = cartProductRepository.searchCartProduct(form.getId());
    Product product = cartProduct.getProduct();
    String size = form.getSize().toUpperCase();
    ProductDetail productDetail = sizeCheck(product, size);

    int quantity = form.getQuantity();
    if (quantity == 0) {
      cartProduct.changeQuantity(quantity);
      deleteProductToCart(form.getId());
      return CartProductDto.from(cartProduct);
    }
    if (quantity > productDetail.getQuantity()) {
      throw new CustomException(ErrorCode.NOT_ENOUGH_PRODUCT);
    }
    cartProduct.changeQuantity(quantity);
    return CartProductDto.from(cartProduct);
  }

  @CacheEvict(value = "cart", allEntries = true)
  @Transactional
  public void deleteProductToCart(Long id) {
    boolean isExist = cartProductRepository.existsById(id);
    if (!isExist) {
      throw new CustomException(ErrorCode.NOT_FOUND_CART_PRODUCT);
    }
    cartProductRepository.deleteById(id);
  }

  private CartProduct quantityCheck(Member member,
                                    Product product,
                                    ProductDetail productDetail,
                                    String size,
                                    int quantity) {
    CartProduct cartProduct = cartProductRepository.findByMemberAndProductAndSize(member, product, size)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CART_PRODUCT));
    if (cartProduct.getQuantity() + quantity > productDetail.getQuantity()) {
      throw new CustomException(ErrorCode.NOT_ENOUGH_PRODUCT);
    }
    cartProduct.addQuantity(quantity);
    return cartProduct;
  }

  private ProductDetail sizeCheck(Product product, String size) {
    return product.getDetails().stream()
            .filter(detail -> detail.getSize().equals(size))
            .findFirst()
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_SIZE));
  }

}
