package com.zb.ecommerce.response;

import org.springframework.http.HttpStatus;

public record HttpApiResponse<T>(T data, String message, HttpStatus status) {
}
