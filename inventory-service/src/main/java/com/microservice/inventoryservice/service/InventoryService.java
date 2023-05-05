package com.microservice.inventoryservice.service;

import com.microservice.inventoryservice.dto.InventoryResponse;
import com.microservice.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {

    @Autowired
    private final InventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    public List<InventoryResponse> isInSock(List<String> skuCodes) {
        return inventoryRepository
                .findBySkuCodeIn(skuCodes).stream().map(item -> InventoryResponse.builder().skuCode(item.getSkuCode()).isInStock(item.getQuantity() > 0).build()).toList();
    }
}
