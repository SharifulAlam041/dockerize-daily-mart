package com.techworld.product.service.repository;

import com.techworld.product.service.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface ProductRepository extends MongoRepository<Product,String> {
}
