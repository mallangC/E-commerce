package com.zb.ecommerce.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageDto<T> {
  private List<T> contents;
  private long totalElements;
  private int totalPages;
  private int number;
  private int size;

  public static <T> PageDto<T> from(Page<T> page) {
    return PageDto.<T>builder()
            .contents(page.getContent())
            .totalElements(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .number(page.getNumber())
            .size(page.getSize())
            .build();
  }

  public static <T> PageDto<T> empty() {
    return PageDto.<T>builder()
            .contents(new ArrayList<>())
            .totalElements(0)
            .totalPages(0)
            .number(0)
            .size(0)
            .build();
  }
}
