package com.microservices.orderservice.service;

import com.microservices.orderservice.dto.InventoryResponse;
import com.microservices.orderservice.dto.OrderLineItemsDto;
import com.microservices.orderservice.dto.OrderRequest;
import com.microservices.orderservice.event.OrderPlaceEvent;
import com.microservices.orderservice.model.Order;
import com.microservices.orderservice.model.OrderLineItems;
import com.microservices.orderservice.repository.OrderRespository;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.service.NullServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class OrderService {
    private final EurekaClient eurekaClient;
    private final OrderRespository orderRespository;

    private final WebClient.Builder webClientBuilder;
    private final KafkaTemplate kafkaTemplate;
    public String placeAnOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        order.setItems(orderRequest.getOrderLineItemsDtoList()
                .stream().map(p -> mapToDto(p, order)).toList());

        var skuCodes = order.getItems().stream().map(p -> p.getSku()).toList();
        //Call inventory service and place order if product is in stock
        InstanceInfo instanceInfo = eurekaClient.getNextServerFromEureka("inventory-service", false);
        var items = webClientBuilder.build().get()
                .uri("http://inventory-service/api/inventory", uriBuilder -> uriBuilder.queryParam("skuCodes", skuCodes).build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();
        var outOfStockItems = Arrays.stream(items).filter(i -> !i.isInStock());
        if (outOfStockItems.count() > 0) {
            throw new IllegalArgumentException("Product is out of stock");
        } else {
            orderRespository.save(order);
            kafkaTemplate.send("notificationTopic", new OrderPlaceEvent(order.getOrderNumber()));
            return "Order placed";
        }
    }
    private OrderLineItems mapToDto(OrderLineItemsDto item, Order order) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setSku(item.getSku());
        orderLineItems.setPrice(item.getPrice());
        orderLineItems.setQuantity(item.getQuantity());
        return orderLineItems;
    }

    public List<Order> getOrders() {
        return orderRespository.findAll();
    }
}
