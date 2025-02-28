package com.zb.ecommerce.service;

import com.zb.ecommerce.domain.dto.ProductDetailDto;
import com.zb.ecommerce.domain.dto.ProductDto;
import com.zb.ecommerce.domain.form.ProductAddForm;
import com.zb.ecommerce.domain.form.ProductDetailAddForm;
import com.zb.ecommerce.domain.form.ProductDetailUpdateForm;
import com.zb.ecommerce.domain.form.ProductUpdateForm;
import com.zb.ecommerce.domain.type.CategoryType;
import com.zb.ecommerce.exception.CustomException;
import com.zb.ecommerce.model.Product;
import com.zb.ecommerce.model.ProductDetail;
import com.zb.ecommerce.repository.ProductDetailRepository;
import com.zb.ecommerce.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.zb.ecommerce.exception.ErrorCode.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

  @Mock
  private ProductRepository productRepository;

  @Mock
  private ProductDetailRepository productDetailRepository;

  @InjectMocks
  private ProductService productService;

  ProductAddForm productAddForm = ProductAddForm.builder()
          .name("신발")
          .code("shoes_mk1")
          .description("너무나 이쁜 신발")
          .price("89000")
          .category("shoes")
          .build();

  ProductDetailAddForm detailAddForm = ProductDetailAddForm.builder()
          .code("shoes_mk1")
          .size("M")
          .quantity("5")
          .build();



  @Test
  @DisplayName("상품 추가 성공")
  void addProduct() {
    //given
    given(productRepository.existsByCode(anyString()))
            .willReturn(false);
    //when
    productService.addProduct(productAddForm);
    //then
    verify(productRepository, times(1)).save(any());
  }

  @Test
  @DisplayName("상품 추가 실패(이미 추가된 상품)")
  void addProductFailure() {
    //given
    given(productRepository.existsByCode(anyString()))
            .willReturn(true);
    try {
      //when
      productService.addProduct(productAddForm);
    } catch (CustomException e) {
      //then
      assertEquals(ALREADY_ADDED_PRODUCT, e.getErrorCode());
      verify(productRepository, times(0)).save(any());
    }
  }

  //-------

  @Test
  @DisplayName("상품 디테일 추가 성공")
  void addProductDetail() {
    //given
    given(productRepository.findByCode(anyString()))
            .willReturn(Optional.of(Product.builder()
                    .id(1L)
                    .name("신발")
                    .code("shoes_mk1")
                    .description("너무 이쁜 신발")
                    .price(89000L)
                    .categoryType(CategoryType.SHOES)
                    .details(null)
                    .build()));
    //when
    productService.addProductDetail(detailAddForm);
    //then
    verify(productDetailRepository, times(1)).save(any());
  }

  @Test
  @DisplayName("상품 디테일 추가 실패(상품을 찾을 수 없음)")
  void addProductDetailFailure1() {
    //given
    given(productRepository.findByCode(anyString()))
            .willReturn(Optional.of(Product.builder()
                    .id(1L)
                    .name("신발")
                    .code("shoes_mk1")
                    .description("너무 이쁜 신발")
                    .price(89000L)
                    .categoryType(CategoryType.SHOES)
                    .details(null)
                    .build()));
    try {
      //when
      productService.addProductDetail(detailAddForm);
    } catch (CustomException e) {
      //then
      assertEquals(NOT_FOUND_PRODUCT, e.getErrorCode());
      verify(productDetailRepository, times(0)).save(any());
    }
  }

  @Test
  @DisplayName("상품 디테일 추가 실패(이미 추가된 상품 디테일)")
  void addProductDetailFailure2() {
    //given
    given(productRepository.findByCode(anyString()))
            .willReturn(Optional.of(Product.builder()
                    .id(1L)
                    .name("신발")
                    .code("shoes_mk1")
                    .description("너무 이쁜 신발")
                    .price(89000L)
                    .categoryType(CategoryType.SHOES)
                    .details(List.of(ProductDetail.builder()
                            .size("M")
                            .quantity(3)
                            .build()))
                    .build()));
    try {
      //when
      productService.addProductDetail(detailAddForm);
    } catch (CustomException e) {
      //then
      assertEquals(ALREADY_ADDED_SIZE, e.getErrorCode());
      verify(productDetailRepository, times(0)).save(any());
    }
  }

  //---------

  @Test
  @DisplayName("모든 상품 확인")
  void getAllProduct() {
    //given
    Sort sort = Sort.by(Sort.Direction.ASC, "name");
    Pageable pageable = PageRequest.of(0, 20, sort);
    Page<Product> expectedPage = new PageImpl<>(List.of(Product.builder()
            .name("신발")
                    .price(89000L)
            .details(List.of(ProductDetail.builder()
                    .size("M")
                    .quantity(3)
                    .build()))
            .build(),
            Product.builder()
                    .name("코트")
                    .price(120000L)
                    .details(new ArrayList<>())
                    .build(),
            Product.builder()
                    .name("벨트")
                    .price(32000L)
                    .details(List.of(ProductDetail.builder()
                            .size("L")
                            .quantity(0)
                            .build()))
                    .build()
            ), pageable, 0);
    given(productRepository.findAll(pageable))
            .willReturn(expectedPage);
    //when
    List<String> result = productService.getAllProduct(0);
    //then
    assertEquals(List.of("신발 : 89,000", "(품절)코트 : 120,000", "(품절)벨트 : 32,000"), result);
    verify(productRepository, times(1)).findAll(pageable);
  }

  @Test
  @DisplayName("모든 상품 확인 (찾을 상품이 없음)")
  void getAllProductFailure1() {
    //given
    Sort sort = Sort.by(Sort.Direction.ASC, "name");
    Pageable pageable = PageRequest.of(0, 20, sort);
    Page<Product> expectedPage = new PageImpl<>(new ArrayList<>(), pageable, 0);
    given(productRepository.findAll(pageable))
            .willReturn(expectedPage);
    //when
    List<String> result = productService.getAllProduct(0);
    //then
    assertNull(result);
    verify(productRepository, times(1)).findAll(pageable);
  }

  //-----------

  @Test
  @DisplayName("디테일 확인")
  void getProductDetail() {
    //given
    given(productRepository.findByCode(anyString()))
            .willReturn(Optional.of(Product.builder()
                    .name("신발")
                    .code("shoes_mk1")
                    .categoryType(CategoryType.SHOES)
                    .price(89000L)
                    .details(List.of(ProductDetail.builder()
                                    .size("M")
                                    .quantity(3)
                                    .build(),
                            ProductDetail.builder()
                                    .size("L")
                                    .quantity(0)
                                    .build()))
                    .build()));
    //when
    ProductDto result = productService.getProductDetail("code");
    //then
    assertEquals("신발", result.getName());
  }

  @Test
  @DisplayName("디테일 확인(디테일 없음)")
  void getProductDetailFailure() {
    //given
    given(productRepository.findByCode(anyString()))
            .willReturn(Optional.of(Product.builder()
                    .name("신발")
                    .price(89000L)
                    .details(new ArrayList<>())
                    .build()));
    //when
    ProductDto result = productService.getProductDetail("code");
    //then
    assertEquals(new ArrayList<>(), result.getDetails());
  }

  //---------

  @Test
  @DisplayName("모든 상품 확인(이름 정렬)(test에선 정렬이 되진 않음)")
  void getAllProductSort() {
    //given
    Sort sort = Sort.by(Sort.Direction.DESC, "name");
    Pageable pageable = PageRequest.of(0, 20, sort);
    Page<Product> expectedPage = new PageImpl<>(List.of(Product.builder()
                    .name("신발")
                    .price(89000L)
                    .details(List.of(ProductDetail.builder()
                            .size("M")
                            .quantity(3)
                            .build()))
                    .build(),
            Product.builder()
                    .name("코트")
                    .price(120000L)
                    .details(new ArrayList<>())
                    .build(),
            Product.builder()
                    .name("벨트")
                    .price(32000L)
                    .details(List.of(ProductDetail.builder()
                            .size("L")
                            .quantity(0)
                            .build()))
                    .build()
    ), pageable, 0);
    given(productRepository.findAll(pageable))
            .willReturn(expectedPage);
    //when
    List<String> result = productService.getAllProductSort(0, "name", false);
    //then
    assertEquals(List.of("신발 : 89,000원", "코트 : 120,000원", "벨트 : 32,000원"),result);
  }

  //--------

  @Test
  @DisplayName("모든 상품 확인(카테고리 정렬)")
  void getAllProductSortCategory() {
    //given
    Sort sort = Sort.by(Sort.Direction.ASC, "name");
    Pageable pageable = PageRequest.of(0, 20, sort);
    Page<Product> expectedPage = new PageImpl<>(List.of(Product.builder()
            .name("벨트")
            .categoryType(CategoryType.BELT)
            .price(32000L)
            .details(List.of(ProductDetail.builder()
                    .size("L")
                    .quantity(0)
                    .build()))
            .build()

    ), pageable, 0);

    given(productRepository.findAllByCategoryType(CategoryType.BELT, pageable))
            .willReturn(expectedPage);
    //when
    List<String> result = productService.getAllProductSortCategory(0, "belt");
    //then
    assertEquals(List.of("벨트 : 32,000원"),result);
  }

  @Test
  @DisplayName("모든 상품 확인(카테고리 정렬)(해당 카테고리 상품이 없음)")
  void getAllProductSortCategory2() {
    //given
    Sort sort = Sort.by(Sort.Direction.ASC, "name");
    Pageable pageable = PageRequest.of(0, 20, sort);
    Page<Product> expectedPage = new PageImpl<>(new ArrayList<>(), pageable, 0);

    given(productRepository.findAllByCategoryType(CategoryType.BELT, pageable))
            .willReturn(expectedPage);
    //when
    List<String> result = productService.getAllProductSortCategory(0, "belt");
    //then
    assertEquals(new ArrayList<>(),result);
  }

  //--------

  @Test
  @DisplayName("상품 수정")
  void updateProduct() {
    //given
    given(productRepository.findByCode(anyString()))
            .willReturn(Optional.of(Product.builder()
                    .code("belt_mk1")
                    .name("벨트")
                    .categoryType(CategoryType.BELT)
                    .price(32000L)
                    .description("짱짱한 벨트")
                    .details(new ArrayList<>())
                    .build()));

    ProductUpdateForm form = ProductUpdateForm.builder()
            .name("신발")
            .code("belt_mk1")
            .changeCode("shoes_mk1")
            .description("너무나 이쁜 신발")
            .price("89000")
            .categoryType("shoes")
            .build();
    //when
    ProductDto result = productService.updateProduct(form);
    //then
    assertEquals("신발",result.getName());
    assertEquals("shoes_mk1",result.getCode());
    assertEquals("너무나 이쁜 신발",result.getDescription());
    assertEquals(CategoryType.SHOES, result.getCategoryType());
    assertEquals(89000L,result.getPrice());
  }

  @Test
  @DisplayName("상품 수정(price, name 빼고 진행)")
  void updateProductFailure1() {
    //given
    given(productRepository.findByCode(anyString()))
            .willReturn(Optional.of(Product.builder()
                    .code("belt_mk1")
                    .name("벨트")
                    .categoryType(CategoryType.BELT)
                    .price(32000L)
                    .description("짱짱한 벨트")
                    .details(new ArrayList<>())
                    .build()));

    ProductUpdateForm form = ProductUpdateForm.builder()
            .code("belt_mk1")
            .changeCode("shoes_mk1")
            .categoryType("shoes")
            .description("너무나 이쁜 신발")
            .build();
    //when
    ProductDto result = productService.updateProduct(form);
    //then
    assertEquals("벨트",result.getName());
    assertEquals("shoes_mk1",result.getCode());
    assertEquals("너무나 이쁜 신발",result.getDescription());
    assertEquals(CategoryType.SHOES,result.getCategoryType());
    assertEquals(32000L,result.getPrice());
  }

  //-------

  @Test
  @DisplayName("디테일 수정")
  void updateProductDetail() {
    //given
    given(productRepository.findByCode(anyString()))
            .willReturn(Optional.of(Product.builder()
                    .name("벨트")
                    .categoryType(CategoryType.BELT)
                    .price(32000L)
                    .details(List.of(ProductDetail.builder()
                            .size("L")
                            .quantity(5)
                            .build()))
                    .build()));

    ProductDetailUpdateForm form = ProductDetailUpdateForm.builder()
            .code("belt_mk1")
            .size("L")
            .changeSize("M")
            .quantity("10")
            .build();

    //when
    ProductDetailDto result = productService.updateProductDetail(form);
    //then
    assertEquals("M",result.getSize());
    assertEquals(10,result.getQuantity());
  }

  @Test
  @DisplayName("디테일 수정(quantity만 수정)")
  void updateProductDetail2() {
    //given
    given(productRepository.findByCode(anyString()))
            .willReturn(Optional.of(Product.builder()
                    .name("벨트")
                    .categoryType(CategoryType.BELT)
                    .price(32000L)
                    .details(List.of(ProductDetail.builder()
                            .size("L")
                            .quantity(5)
                            .build()))
                    .build()));

    ProductDetailUpdateForm form = ProductDetailUpdateForm.builder()
            .code("belt_mk1")
            .size("L")
            .quantity("10")
            .build();

    //when
    ProductDetailDto result = productService.updateProductDetail(form);
    //then
    assertEquals("L",result.getSize());
    assertEquals(10,result.getQuantity());
  }

  @Test
  @DisplayName("디테일 수정 실패(맞는 size가 없음)")
  void updateProductDetailFailure() {
    //given
    given(productRepository.findByCode(anyString()))
            .willReturn(Optional.of(Product.builder()
                    .name("벨트")
                    .categoryType(CategoryType.BELT)
                    .price(32000L)
                    .details(List.of(ProductDetail.builder()
                            .size("M")
                            .quantity(5)
                            .build()))
                    .build()));

    ProductDetailUpdateForm form = ProductDetailUpdateForm.builder()
            .code("belt_mk1")
            .size("L")
            .quantity("10")
            .build();

    try {
      //when
      productService.updateProductDetail(form);
    } catch (CustomException e){
      //then
      assertEquals(NOT_FOUND_SIZE, e.getErrorCode());
    }
  }

  //------

  @Test
  @DisplayName("상품 삭제")
  void deleteProduct() {
    //given
    given(productRepository.findByCode(anyString()))
            .willReturn(Optional.of(Product.builder()
                    .name("벨트")
                    .code("belt_mk1")
                    .categoryType(CategoryType.BELT)
                    .price(32000L)
                    .details(List.of(ProductDetail.builder()
                            .size("L")
                            .quantity(5)
                            .build()))
                    .build()));
    //when
    productService.deleteProduct("belt_mk1");
    //then
    verify(productRepository, times(1)).findByCode(anyString());
    verify(productRepository, times(1)).deleteByCode(anyString());
  }

//-----

  @Test
  @DisplayName("상품 디테일 삭제")
  void deleteProductDetail() {
    //given
    given(productRepository.findByCode(anyString()))
            .willReturn(Optional.of(Product.builder()
                    .name("벨트")
                    .code("belt_mk1")
                    .categoryType(CategoryType.BELT)
                    .price(32000L)
                    .details(List.of(ProductDetail.builder()
                            .size("L")
                            .quantity(5)
                            .build()))
                    .build()));

    ProductDetailUpdateForm form = ProductDetailUpdateForm.builder()
            .code("belt_mk1")
            .size("L")
            .build();
    //when
    ProductDetailDto result = productService.deleteProductDetail(form);

    //then
    assertEquals("L",result.getSize());
    assertEquals(5,result.getQuantity());
  }

}