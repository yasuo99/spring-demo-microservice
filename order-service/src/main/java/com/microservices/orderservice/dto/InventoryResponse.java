package com.microservices.orderservice.dto;

import lombok.Data;

@Data
public class InventoryResponse {
    private String skuCode;
    private boolean isInStock;
}
