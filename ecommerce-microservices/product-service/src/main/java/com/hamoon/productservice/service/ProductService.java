package com.hamoon.productservice.service;

import com.hamoon.productservice.dto.ProductRequest;
import com.hamoon.productservice.dto.ProductResponse;
import com.hamoon.productservice.model.Product;
import com.hamoon.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public ProductResponse createProduct(ProductRequest productRequest) {
        Product product = Product.builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .build();

        Product saved = productRepository.save(product);
        ProductResponse productResponse = mapProductToProductResponse(saved);
        log.info("Product with id {} was created successfully", product.getId());
        return productResponse;
    }

    public List<ProductResponse> getAllProducts () {
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(this::mapProductToProductResponse)
                .toList();
    }

    private ProductResponse mapProductToProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .build();
    }

}
