package com.zb.ecommerce.domain.type;

public enum CategoryType {
  TOP,
  BOTTOM,
  SHOES,
  SOCKS,
  BELT,
  HAT,
  ACCESSORIES,
  OTHERS;

  public static CategoryType fromString(String category) {
    String s = category.toUpperCase();
    for (CategoryType type : CategoryType.values()) {
      if (type.toString().equals(s)) {
        return type;
      }
    }
    return OTHERS;
  }
}
