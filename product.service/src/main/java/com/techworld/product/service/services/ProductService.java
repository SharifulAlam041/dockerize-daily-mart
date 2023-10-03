package com.techworld.product.service.services;

import com.techworld.product.service.dto.ProductRequest;
import com.techworld.product.service.dto.ProductResponse;

import java.util.List;

public interface ProductService {

    void createProduct(ProductRequest productRequest);
    List<ProductResponse> getAllProducts();

}
