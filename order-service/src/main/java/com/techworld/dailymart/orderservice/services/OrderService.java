package com.techworld.dailymart.orderservice.services;

import com.techworld.dailymart.orderservice.dto.OrderRequest;

public interface OrderService {
    String placeOrder(OrderRequest orderRequest);
}
