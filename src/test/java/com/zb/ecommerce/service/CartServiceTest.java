package com.zb.ecommerce.service;

import com.zb.ecommerce.domain.dto.CartProductDto;
import com.zb.ecommerce.domain.form.CartAddForm;
import com.zb.ecommerce.domain.form.CartUpdateForm;
import com.zb.ecommerce.domain.type.CategoryType;
import com.zb.ecommerce.exception.CustomException;
import com.zb.ecommerce.model.CartProduct;
import com.zb.ecommerce.model.Member;
import com.zb.ecommerce.model.Product;
import com.zb.ecommerce.model.ProductDetail;
import com.zb.ecommerce.repository.CartProductRepository;
import com.zb.ecommerce.repository.MemberRepository;
import com.zb.ecommerce.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.zb.ecommerce.exception.ErrorCode.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class CartServiceTest {

  @Mock
  private MemberRepository memberRepository;

  @Mock
  private CartProductRepository cartProductRepository;

  @Mock
  private ProductRepository productRepository;

  @InjectMocks
  private CartService cartService;

  Member memberBase = Member.builder()
          .id(1L)
          .email("test@test.com")
          .cart(new ArrayList<>())
          .build();

  Product productBase = Product.builder()
          .id(1L)
          .name("신발")
          .price(57000L)
          .code("shoes_mk1")
          .categoryType(CategoryType.SHOES)
          .description("넘나 이쁜 신발")
          .details(List.of(ProductDetail.builder()
                  .size("L")
                  .quantity(5)
                  .build()))
          .build();

  CartAddForm cartAddForm = CartAddForm.builder()
          .productCode("shoes_mk1")
          .size("L")
          .quantity(1)
          .build();


  @Test
  @DisplayName("카트 상품 추가")
  void addProductToCart1() {
    //given
    given(memberRepository.searchMemberByEmail(anyString()))
            .willReturn(Optional.ofNullable(memberBase));

    given(productRepository.searchProductByCode(anyString()))
            .willReturn(Optional.ofNullable(productBase));

    given(cartProductRepository.save(any()))
            .willReturn(CartProduct.builder()
                    .id(1L)
                    .product(Product.builder()
                            .id(1L)
                            .name("신발")
                            .price(57000L)
                            .code("shoes_mk1")
                            .categoryType(CategoryType.SHOES)
                            .description("넘나 이쁜 신발")
                            .details(List.of(ProductDetail.builder()
                                    .size("L")
                                    .quantity(5)
                                    .build()))
                            .build())
                    .size("L")
                    .quantity(1)
                    .build());

    //when
    CartProductDto result = cartService.addProductToCart(cartAddForm, memberBase.getEmail());
    //then
    assertEquals(1L, result.getId());
    assertEquals("신발", result.getProductName());
    assertEquals("L", result.getSize());
    assertEquals(1, result.getQuantity());

    verify(cartProductRepository, times(1)).save(any());
  }

  @Test
  @DisplayName("카트 상품 추가(이미 카트에 상품이 있음)")
  void addProductToCart2() {
    //given
    given(memberRepository.searchMemberByEmail(anyString()))
            .willReturn(Optional.ofNullable(Member.builder()
                    .id(1L)
                    .email("test@test.com")
                    .cart(List.of(CartProduct.builder()
                            .id(1L)
                            .product(Product.builder()
                                    .id(1L)
                                    .name("신발")
                                    .price(57000L)
                                    .code("shoes_mk1")
                                    .categoryType(CategoryType.SHOES)
                                    .description("넘나 이쁜 신발")
                                    .details(List.of(ProductDetail.builder()
                                            .size("L")
                                            .quantity(5)
                                            .build()))
                                    .build())
                            .size("L")
                            .quantity(1)
                            .build()))
                    .build()));

    given(productRepository.searchProductByCode(anyString()))
            .willReturn(Optional.ofNullable(productBase));

    given(cartProductRepository.findByMemberAndProductAndSize(any(), any(), anyString()))
            .willReturn(Optional.of(CartProduct.builder()
                    .id(1L)
                    .product(Product.builder()
                            .id(1L)
                            .name("신발")
                            .price(57000L)
                            .code("shoes_mk1")
                            .categoryType(CategoryType.SHOES)
                            .description("넘나 이쁜 신발")
                            .details(List.of(ProductDetail.builder()
                                    .size("L")
                                    .quantity(5)
                                    .build()))
                            .build())
                    .size("L")
                    .quantity(1)
                    .build()));

    //when
    CartProductDto result = cartService.addProductToCart(cartAddForm, memberBase.getEmail());
    //then
    assertEquals(1L, result.getId());
    assertEquals("신발", result.getProductName());
    assertEquals("L", result.getSize());
    assertEquals(2, result.getQuantity());

    verify(cartProductRepository, times(0)).save(any());
  }

  @Test
  @DisplayName("카트 상품 추가 실패(잘못된 이메일 입력)")
  void addProductToCartFailure1() {
    //given
    given(memberRepository.searchMemberByEmail(anyString()))
            .willThrow(new CustomException(NOT_FOUND_MEMBER));

    try {
      //when
      cartService.addProductToCart(cartAddForm, memberBase.getEmail());
    } catch (CustomException e) {
      //then
      assertEquals(NOT_FOUND_MEMBER, e.getErrorCode());
    }
    verify(cartProductRepository, times(0)).save(any());
  }

  @Test
  @DisplayName("카트 상품 추가 실패(잘못된 코드 입력)")
  void addProductToCartFailure2() {
    //given
    given(memberRepository.searchMemberByEmail(anyString()))
            .willReturn(Optional.ofNullable(memberBase));

    given(productRepository.searchProductByCode(anyString()))
            .willThrow(new CustomException(NOT_FOUND_PRODUCT));
    try {
      //when
      cartService.addProductToCart(cartAddForm, memberBase.getEmail());
    } catch (CustomException e) {
      //then
      assertEquals(NOT_FOUND_PRODUCT, e.getErrorCode());
    }
    verify(cartProductRepository, times(0)).save(any());
  }


  @Test
  @DisplayName("카트 상품 추가 실패(이미 카트에 상품이 있고 상품 갯수가 부족함)")
  void addProductToCartFailure4() {
    //given
    given(memberRepository.searchMemberByEmail(anyString()))
            .willReturn(Optional.ofNullable(Member.builder()
                    .id(1L)
                    .email("test@test.com")
                    .cart(List.of(CartProduct.builder()
                            .id(1L)
                            .product(Product.builder()
                                    .id(1L)
                                    .name("신발")
                                    .price(57000L)
                                    .code("shoes_mk1")
                                    .categoryType(CategoryType.SHOES)
                                    .description("넘나 이쁜 신발")
                                    .details(List.of(ProductDetail.builder()
                                            .size("L")
                                            .quantity(1)
                                            .build()))
                                    .build())
                            .size("L")
                            .quantity(1)
                            .build()))
                    .build()));

    given(productRepository.searchProductByCode(anyString()))
            .willReturn(Optional.ofNullable(Product.builder()
                    .id(1L)
                    .name("신발")
                    .price(57000L)
                    .code("shoes_mk1")
                    .categoryType(CategoryType.SHOES)
                    .description("넘나 이쁜 신발")
                    .details(List.of(ProductDetail.builder()
                            .size("L")
                            .quantity(1)
                            .build()))
                    .build()));


    given(cartProductRepository.findByMemberAndProductAndSize(any(), any(), anyString()))
            .willReturn(Optional.of(CartProduct.builder()
                    .id(1L)
                    .product(Product.builder()
                            .id(1L)
                            .name("신발")
                            .price(57000L)
                            .code("shoes_mk1")
                            .categoryType(CategoryType.SHOES)
                            .description("넘나 이쁜 신발")
                            .details(List.of(ProductDetail.builder()
                                    .size("L")
                                    .quantity(1)
                                    .build()))
                            .build())
                    .size("L")
                    .quantity(1)
                    .build()));


    try {
      //when
      cartService.addProductToCart(cartAddForm, memberBase.getEmail());
    } catch (CustomException e) {
      //then
      assertEquals(NOT_ENOUGH_PRODUCT, e.getErrorCode());
    }
    verify(cartProductRepository, times(0)).save(any());
  }

  //-------

  @Test
  @DisplayName("카트에 모든 상품 확인")
  void getAllCartProducts1() {
    //given
    Pageable pageable = PageRequest.of(0, 20);
    given(cartProductRepository.searchCartProductsByEmail(anyInt(), anyString()))
            .willReturn(new PageImpl<>(List.of(CartProduct.builder()
                    .id(1L)
                    .member(Member.builder()
                            .id(1L)
                            .email("test@test.com")
                            .build())
                    .product(Product.builder()
                            .id(1L)
                            .name("신발")
                            .price(57000L)
                            .build())
                    .size("L")
                    .quantity(2)
                    .build(), CartProduct.builder()
                    .id(2L)
                    .member(Member.builder()
                            .id(1L)
                            .email("test@test.com")
                            .build())
                    .product(Product.builder()
                            .id(2L)
                            .name("벨트")
                            .price(27000L)
                            .build())
                    .size("M")
                    .quantity(1)
                    .build()), pageable, 2));

    //when
    Page<CartProductDto> result = cartService.getAllCartProducts(0, "email");
    //then
    assertEquals(2, result.getTotalElements());
    assertEquals("신발", result.getContent().get(0).getProductName());
    assertEquals("L", result.getContent().get(0).getSize());
    assertEquals(2, result.getContent().get(0).getQuantity());
  }


  @Test
  @DisplayName("카트에 모든 상품 확인(카트에 아무것도 없음)")
  void getAllCartProducts2() {
    //given
    given(cartProductRepository.searchCartProductsByEmail(anyInt(), anyString()))
            .willReturn(new PageImpl<>(new ArrayList<>()));
    //when
    Page<CartProductDto> result = cartService.getAllCartProducts(0, "email");
    //then
    assertEquals(0, result.getTotalElements());
  }

  //----------

  @Test
  @DisplayName("카드 상품 갯수 수정")
  void updateCartProduct() {
    //given
    given(cartProductRepository.searchCartProductById(anyLong()))
            .willReturn(Optional.ofNullable(CartProduct.builder()
                    .id(1L)
                    .member(memberBase)
                    .product(Product.builder()
                            .id(1L)
                            .name("신발")
                            .price(57000L)
                            .code("shoes_mk1")
                            .categoryType(CategoryType.SHOES)
                            .description("넘나 이쁜 신발")
                            .details(List.of(ProductDetail.builder()
                                    .size("M")
                                    .quantity(5)
                                    .build()))
                            .build())
                    .size("M")
                    .quantity(1)
                    .build()));

    CartUpdateForm form = CartUpdateForm.builder()
            .id(1L)
            .size("M")
            .quantity(2)
            .build();
    //when
    CartProductDto result = cartService.updateProductToCart(form, memberBase.getEmail());
    //then
    assertEquals(2, result.getQuantity());
  }

  @Test
  @DisplayName("카드 상품 갯수 수정 실패(잘못된 아이디 입력)")
  void updateCartProductFailure1() {
    //given
    given(cartProductRepository.searchCartProductById(anyLong()))
            .willThrow(new CustomException(NOT_FOUND_CART_PRODUCT));

    CartUpdateForm form = CartUpdateForm.builder()
            .id(1L)
            .size("M")
            .quantity(2)
            .build();

    try {
      //when
      cartService.updateProductToCart(form, "email");
    } catch (CustomException e) {
      //then
      assertEquals(NOT_FOUND_CART_PRODUCT, e.getErrorCode());
    }
  }

  @Test
  @DisplayName("카드 상품 갯수 수정 실패(잘못된 사이즈 입력)")
  void updateCartProductFailure2() {
    //given
    given(cartProductRepository.searchCartProductById(anyLong()))
            .willReturn(Optional.ofNullable(CartProduct.builder()
                    .id(1L)
                    .member(memberBase)
                    .product(Product.builder()
                            .id(1L)
                            .name("신발")
                            .price(57000L)
                            .code("shoes_mk1")
                            .categoryType(CategoryType.SHOES)
                            .description("넘나 이쁜 신발")
                            .details(List.of(ProductDetail.builder()
                                    .size("M")
                                    .quantity(5)
                                    .build()))
                            .build())
                    .size("M")
                    .quantity(1)
                    .build()));

    CartUpdateForm form = CartUpdateForm.builder()
            .id(1L)
            .size("L")
            .quantity(2)
            .build();
    try {
      //when
      cartService.updateProductToCart(form, memberBase.getEmail());
    } catch (CustomException e) {
      //then
      assertEquals(NOT_FOUND_SIZE, e.getErrorCode());
    }
  }

  @Test
  @DisplayName("카드 상품 갯수 수정 실패(상품 갯수 부족)")
  void updateCartProductFailure3() {
    //given
    given(cartProductRepository.searchCartProductById(anyLong()))
            .willReturn(Optional.ofNullable(CartProduct.builder()
                    .id(1L)
                    .member(Member.builder()
                            .email("wrongEmail")
                            .build())
                    .product(Product.builder()
                            .id(1L)
                            .name("신발")
                            .price(57000L)
                            .code("shoes_mk1")
                            .categoryType(CategoryType.SHOES)
                            .description("넘나 이쁜 신발")
                            .details(List.of(ProductDetail.builder()
                                    .size("M")
                                    .quantity(5)
                                    .build()))
                            .build())
                    .size("M")
                    .quantity(1)
                    .build()));

    CartUpdateForm form = CartUpdateForm.builder()
            .id(1L)
            .size("M")
            .quantity(10)
            .build();
    //when
    CustomException exception = assertThrows(CustomException.class,
            () -> cartService.updateProductToCart(form, memberBase.getEmail()));
    //then
    assertEquals(CART_DO_NOT_HAVE_PRODUCT, exception.getErrorCode());

  }

  //----

  @Test
  @DisplayName("카트 상품 삭제")
  void deleteCartProduct() {
    //given
    given(cartProductRepository.searchCartProductById(anyLong()))
            .willReturn(Optional.of(CartProduct.builder()
                    .id(1L)
                    .member(memberBase)
                    .product(Product.builder()
                            .id(1L)
                            .name("신발")
                            .price(57000L)
                            .code("shoes_mk1")
                            .categoryType(CategoryType.SHOES)
                            .description("넘나 이쁜 신발")
                            .details(List.of(ProductDetail.builder()
                                    .size("M")
                                    .quantity(5)
                                    .build()))
                            .build())
                    .size("M")
                    .quantity(1)
                    .build()));
    //when
    cartService.deleteProductToCart(1L, memberBase.getEmail());
    //then
    verify(cartProductRepository, times(1)).delete(any());
  }

  @Test
  @DisplayName("카트 상품 삭제 실패(잘못된 ID입력)")
  void deleteCartProductFailure1() {
    //given
    given(cartProductRepository.searchCartProductById(anyLong()))
            .willReturn(Optional.empty());

    //when
    CustomException exception = assertThrows(CustomException.class,
            () -> cartService.deleteProductToCart(1L, memberBase.getEmail()));

    //then
    assertEquals(NOT_FOUND_CART_PRODUCT, exception.getErrorCode());
  }

  @Test
  @DisplayName("카트 상품 삭제 실패(내 카트 상품이 아님)")
  void deleteCartProductFailure2() {
    //given
    given(cartProductRepository.searchCartProductById(anyLong()))
            .willReturn(Optional.of(CartProduct.builder()
                    .member(Member.builder()
                            .email("wrongEmail")
                            .build())
                    .build()));
    //when
    CustomException exception = assertThrows(CustomException.class,
            () -> cartService.deleteProductToCart(1L, memberBase.getEmail()));
    //then
    assertEquals(CART_DO_NOT_HAVE_PRODUCT, exception.getErrorCode());
  }


}