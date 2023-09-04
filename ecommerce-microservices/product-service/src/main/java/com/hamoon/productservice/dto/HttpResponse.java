package com.hamoon.productservice.dto;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class HttpResponse <T> {
    private String message;
    private T data;
}
