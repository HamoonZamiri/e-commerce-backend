package com.hamoon.productservice.controller;

import com.hamoon.productservice.dto.HttpResponse;
import com.hamoon.productservice.dto.ProductRequest;
import com.hamoon.productservice.dto.ProductResponse;
import com.hamoon.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    // here productRequest represents a DTO class
    public ProductResponse createProduct(@RequestBody ProductRequest productRequest){
        return productService.createProduct(productRequest);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public HttpResponse<List<ProductResponse>> getAllProducts(){
        List<ProductResponse> products = productService.getAllProducts();
        return new HttpResponse<>("Products retrieved successfully", products);
    }
}
