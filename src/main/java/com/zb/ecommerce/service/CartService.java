package com.zb.ecommerce.service;

import com.zb.ecommerce.domain.dto.CartProductDto;
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
    Member member = memberRepository.searchByEmail(email);
    Product product = productRepository.searchByCode(form.getProductCode());
    int quantity = form.getQuantity();

    String size = form.getSize().toUpperCase();
    ProductDetail productDetail = sizeCheck(product, size);

    boolean isExist = member.getCart().stream()
            .anyMatch(item -> Objects.equals(item.getProduct().getId(), product.getId()));

    if (isExist) {
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

  @Cacheable(value = "cart", key = "'cart-all-'+#email")
  public List<CartProductDto> getAllCartProducts(String email) {
    Member member = memberRepository.searchByEmail(email);
    return member.getCart().stream()
            .map(CartProductDto::from)
            .toList();
  }

  @Transactional
  @CacheEvict(value = "cart", allEntries = true)
  public CartProductDto updateProductToCart(CartUpdateForm form) {
    CartProduct cartProduct = cartProductRepository.findById(form.getId())
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CART_PRODUCT));
    Product product = cartProduct.getProduct();
    String size = form.getSize().toUpperCase();
    ProductDetail productDetail = sizeCheck(product, size);

    int quantity = form.getQuantity();

    if (quantity > productDetail.getQuantity()) {
      throw new CustomException(ErrorCode.NOT_ENOUGH_PRODUCT);
    }
    cartProduct.setQuantity(quantity);
    return CartProductDto.from(cartProduct);
  }

  @Transactional
  @CacheEvict(value = "cart", allEntries = true)
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
    CartProduct cartProduct =  cartProductRepository.findByMemberAndProductAndSize(member, product, size)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CART_PRODUCT));
    if (cartProduct.getQuantity() + quantity > productDetail.getQuantity()) {
      throw new CustomException(ErrorCode.NOT_ENOUGH_PRODUCT);
    }
    cartProduct.setQuantity(cartProduct.getQuantity() + quantity);
    return cartProduct;
  }

  private ProductDetail sizeCheck(Product product, String size){
    return product.getDetails().stream()
            .filter(detail -> detail.getSize().equals(size))
            .findFirst()
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_SIZE));
  }

}
