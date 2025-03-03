package com.zb.ecommerce.domain.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Builder
public class PageDto {
  private List<?> content;
  private long totalElements;
  private int totalPages;
  private int number;
  private int size;

  public static PageDto from(Page<?> page) {
    return PageDto.builder()
            .content(page.getContent())
            .totalElements(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .number(page.getNumber())
            .size(page.getSize())
            .build();
  }
}
