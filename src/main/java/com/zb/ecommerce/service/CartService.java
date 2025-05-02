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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
  public CartProductDto addProductToCart(CartAddForm form, String email) {
    Member member = searchMemberByEmail(email);
    Product product = searchProductByCode(form.getProductCode());
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
  public Page<CartProductDto> getAllCartProducts(int page, String email) {
    if (email == null) {
      return new PageImpl<>(List.of(), Pageable.ofSize(20), 0);
    }
    Page<CartProduct> cartProducts = cartProductRepository.searchCartProductsByEmail(page, email);
    List<CartProductDto> cartProductDtos = cartProducts.stream()
            .map(CartProductDto::from)
            .toList();
    return new PageImpl<>(cartProductDtos,
            cartProducts.getPageable(), cartProducts.getTotalElements());
  }

  @CacheEvict(value = "cart", allEntries = true)
  @Transactional
  public CartProductDto updateProductToCart(CartUpdateForm form, String email) {
    CartProduct cartProduct = searchCartProductByIdValidation(form.getId(), email);

    Product product = cartProduct.getProduct();
    String size = form.getSize().toUpperCase();
    ProductDetail productDetail = sizeCheck(product, size);

    int quantity = form.getQuantity();
    if (quantity == 0) {
      cartProduct.changeQuantity(quantity);
      deleteProductToCart(form.getId(), email);
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
  public void deleteProductToCart(Long id, String email) {
    cartProductRepository.delete(searchCartProductByIdValidation(id, email));
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

  private Member searchMemberByEmail(String email) {
    return memberRepository.searchMemberByEmail(email)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MEMBER));
  }

  private CartProduct searchCartProductByIdValidation(Long id, String email) {
    CartProduct cartProduct =  cartProductRepository.searchCartProductById(id)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CART_PRODUCT));
    if (!cartProduct.getMember().getEmail().equals(email)) {
      throw new CustomException(ErrorCode.CART_DO_NOT_HAVE_PRODUCT);
    }
    return cartProduct;
  }
  private Product searchProductByCode(String code) {
    return productRepository.searchProductByCode(code)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PRODUCT));
  }

}
