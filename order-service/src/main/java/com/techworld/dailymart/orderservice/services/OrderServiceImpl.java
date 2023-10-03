package com.techworld.dailymart.orderservice.services;

import brave.Span;
import brave.Tracer;
import com.techworld.dailymart.orderservice.dto.InventoryResponse;
import com.techworld.dailymart.orderservice.dto.OrderLineItemDto;
import com.techworld.dailymart.orderservice.dto.OrderRequest;
import com.techworld.dailymart.orderservice.event.OrderPlacedEvent;
import com.techworld.dailymart.orderservice.model.Order;
import com.techworld.dailymart.orderservice.model.OrderLineItems;
import com.techworld.dailymart.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;
    private final KafkaTemplate<String,OrderPlacedEvent> kafkaTemplate;
    //private final Tracer tracer;

    @Override
    public String placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemDtoList()
                .stream()
                .map(this::mapToDto)
                .toList();
        order.setOrderLineItemsList(orderLineItems);

        List<String> skuCodes = order.getOrderLineItemsList().stream()
                .map(OrderLineItems::getSkuCode)
                .toList();

//        Span inventoryServiceLookup= null;
//        try(Tracer.SpanInScope spanInScope= tracer.withSpanInScope(inventoryServiceLookup.start())) {
//            //call inventory service
//            inventoryServiceLookup = tracer.nextSpan().name("InventoryServiceLookup");
            InventoryResponse[] inventoryResponseArray = webClientBuilder.build().get()
                    .uri("http://inventory-service/api/inventory",
                            uriBuilder -> uriBuilder.queryParam("skuCodes", skuCodes).build())
                    .retrieve()
                    .bodyToMono(InventoryResponse[].class)
                    .block();

            if (inventoryResponseArray != null) {
                boolean allProductsIsInStock = Arrays.stream(inventoryResponseArray)
                        .allMatch(InventoryResponse::isInStock);
                if(allProductsIsInStock) {
                    orderRepository.save(order);
                    kafkaTemplate.send("notificationTopic",new OrderPlacedEvent(order.getOrderNumber()));
                    return "Order placed successfully.";
                } else {
                    throw new IllegalArgumentException("product is not in stock.Please try again later.");
                }
            } else {
                throw new NullPointerException("No Inventory Data");
            }
//        } finally {
//            inventoryServiceLookup.finish();
//        }

    }

    private OrderLineItems mapToDto(OrderLineItemDto orderLineItemDto){
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineItemDto.getPrice());
        orderLineItems.setQuantity(orderLineItemDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemDto.getSkuCode());
        return orderLineItems;
    }
}
