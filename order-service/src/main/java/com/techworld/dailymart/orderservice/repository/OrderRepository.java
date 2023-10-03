package com.techworld.dailymart.orderservice.repository;

import com.techworld.dailymart.orderservice.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order,Long> {
}
