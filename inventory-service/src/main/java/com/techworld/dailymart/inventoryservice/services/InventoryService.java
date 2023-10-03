package com.techworld.dailymart.inventoryservice.services;

import com.techworld.dailymart.inventoryservice.dto.InventoryResponse;

import java.util.List;

public interface InventoryService {
    List<InventoryResponse> isInStock(List<String> skuCodes);
}
